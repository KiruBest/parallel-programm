package lab3;

import org.jocl.*;
import utils.ImageUtils;
import utils.StringExtensions;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

import static constants.Path.*;
import static org.jocl.CL.*;
import static utils.ImageUtils.createBufferedImage;

public class ImageSample {
    /**
     * The input image
     */
    private final BufferedImage inputImage;

    /**
     * The output image
     */
    private final BufferedImage invertedImage;

    private final int contrastedImageWidth;
    private final int contrastedImageHeight;
    private final BufferedImage contrastedImage;

    /**
     * The OpenCL context
     */
    private cl_context context;

    /**
     * The OpenCL command queue
     */
    private cl_command_queue commandQueue;

    /**
     * The OpenCL kernel
     */
    private cl_kernel kernelContrastedImage;

    private cl_kernel kernelInvertedImage;

    /**
     * The memory object for the input image
     */
    private cl_mem inputImageMem;

    /**
     * The memory object for the output image
     */
    private cl_mem invertedImageMem;

    private cl_mem contrastedImageMem;

    /**
     * The width of the image
     */
    private final int imageSizeX;

    /**
     * The height of the image
     */
    private final int imageSizeY;

    public ImageSample(String fileName) {
        System.out.println("Обработка файла: " + fileName + "...");

        // Read the input image file and create the output images
        inputImage = createBufferedImage(IMG_PATH + fileName);
        assert inputImage != null;
        imageSizeX = inputImage.getWidth();
        imageSizeY = inputImage.getHeight();
        int transformMatrixSize = 3;

        contrastedImageWidth = imageSizeX - transformMatrixSize + 1;
        contrastedImageHeight = imageSizeY - transformMatrixSize + 1;

        invertedImage = new BufferedImage(imageSizeX, imageSizeY, BufferedImage.TYPE_INT_RGB);
        contrastedImage = new BufferedImage(contrastedImageWidth, contrastedImageHeight, BufferedImage.TYPE_INT_RGB);

        initCL("ProgramSourceTaskA");
        initImageMem();

        long time = System.currentTimeMillis();

        contrastImage();
        invertImage();

        System.out.println("Время обработки = " + (System.currentTimeMillis() - time) + "\n");
        try {
            ImageUtils.saveImage(contrastedImage, IMG_PATH + CONTRAST_IMG_PATH + "gpu_" + fileName);
            ImageUtils.saveImage(invertedImage, IMG_PATH + INVERTED_IMG_PATH + "gpu_" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the OpenCL context, command queue and kernel
     */
    void initCL(String fileName) {
        String programSource = StringExtensions.getTextFromFile(NATIVE_CODE_PATH + fileName);
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_GPU;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int[] numPlatformsArray = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int[] numDevicesArray = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id[] devices = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Check if images are supported
        int[] imageSupport = new int[1];
        clGetDeviceInfo(device, CL.CL_DEVICE_IMAGE_SUPPORT,
                Sizeof.cl_int, Pointer.to(imageSupport), null);
        System.out.println("Images supported: " + (imageSupport[0] == 1));
        if (imageSupport[0] == 0) {
            System.out.println("Images are not supported");
            System.exit(1);
            return;
        }

        // Create a command-queue for the selected device
        cl_queue_properties properties = new cl_queue_properties();
        properties.addProperty(CL_QUEUE_PROFILING_ENABLE, 1);
        properties.addProperty(CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE, 1);
        commandQueue = clCreateCommandQueueWithProperties(
                context, device, properties, null);

        // Create the program
        System.out.println("Creating program...");
        cl_program program = clCreateProgramWithSource(context,
                1, new String[]{programSource}, null, null);

        // Build the program
        System.out.println("Building program...");
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        System.out.println("Creating kernel...");
        kernelContrastedImage = clCreateKernel(program, "transformImage", null);
        kernelInvertedImage = clCreateKernel(program, "invertImage", null);
    }

    /**
     * Initialize the memory objects for the input and output images
     */
    private void initImageMem() {
        // Create the memory object for the input- and output image
        DataBufferInt dataBufferSrc = (DataBufferInt) inputImage.getRaster().getDataBuffer();
        int[] dataSrc = dataBufferSrc.getData();

        cl_image_format imageFormat = new cl_image_format();
        imageFormat.image_channel_order = CL_RGBA;
        imageFormat.image_channel_data_type = CL_UNSIGNED_INT8;

        inputImageMem = clCreateImage2D(
                context, CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR,
                new cl_image_format[]{imageFormat}, imageSizeX, imageSizeY,
                (long) imageSizeX * Sizeof.cl_uint, Pointer.to(dataSrc), null);

        invertedImageMem = clCreateImage2D(
                context, CL_MEM_WRITE_ONLY,
                new cl_image_format[]{imageFormat}, imageSizeX, imageSizeY,
                0, null, null);

        contrastedImageMem = clCreateImage2D(
                context, CL_MEM_WRITE_ONLY,
                new cl_image_format[]{imageFormat}, contrastedImageWidth, contrastedImageHeight,
                0, null, null);
    }

    void contrastImage() {
        // Set up the work size and arguments, and execute the kernel
        long[] globalWorkSize = new long[2];
        globalWorkSize[0] = contrastedImageWidth;
        globalWorkSize[1] = contrastedImageHeight;

        clSetKernelArg(kernelContrastedImage, 0, Sizeof.cl_mem, Pointer.to(inputImageMem));
        clSetKernelArg(kernelContrastedImage, 1, Sizeof.cl_mem, Pointer.to(contrastedImageMem));

        clEnqueueNDRangeKernel(commandQueue, kernelContrastedImage, 2, null,
                globalWorkSize, null, 0, null, null);

        // Read the pixel data into the output image
        DataBufferInt contrastedBufferDst = (DataBufferInt) contrastedImage.getRaster().getDataBuffer();
        int[] contrastedDst = contrastedBufferDst.getData();

        clEnqueueReadImage(
                commandQueue, contrastedImageMem, true, new long[3],
                new long[]{contrastedImageWidth, contrastedImageHeight, 1},
                (long) contrastedImageWidth * Sizeof.cl_uint, 0,
                Pointer.to(contrastedDst), 0, null, null);
    }

    void invertImage() {
        // Set up the work size and arguments, and execute the kernel
        long[] globalWorkSize = new long[2];
        globalWorkSize[0] = imageSizeX;
        globalWorkSize[1] = imageSizeY;

        clSetKernelArg(kernelInvertedImage, 0, Sizeof.cl_mem, Pointer.to(inputImageMem));
        clSetKernelArg(kernelInvertedImage, 1, Sizeof.cl_mem, Pointer.to(invertedImageMem));

        clEnqueueNDRangeKernel(commandQueue, kernelInvertedImage, 2, null,
                globalWorkSize, null, 0, null, null);

        DataBufferInt invertedBufferDst = (DataBufferInt) invertedImage.getRaster().getDataBuffer();
        int[] invertedDst = invertedBufferDst.getData();

        clEnqueueReadImage(
                commandQueue, invertedImageMem, true, new long[3],
                new long[]{imageSizeX, imageSizeY, 1},
                (long) imageSizeX * Sizeof.cl_uint, 0,
                Pointer.to(invertedDst), 0, null, null);
    }
}
