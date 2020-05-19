package mcib3d.tapas.ext.CLIJ.plugins;

import ij.IJ;
import mcib3d.tapas.ext.CLIJ.core.CLIJ_Utils;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.ext.CLIJ.core.TapasProcessingCLIJ;

import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;

import java.util.HashMap;

public class FilterProcess implements TapasProcessingCLIJ {
    public final static String RADIUSXY = "radxy";
    public final static String RADIUSZ = "radz";
    public final static String FILTER = "filter";
    HashMap<String, String> parameters;

    public FilterProcess() {
        parameters = new HashMap<>();
        setParameter(RADIUSXY, "2");
        setParameter(RADIUSZ, "0");
        setParameter(FILTER, "median");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case RADIUSXY:
                parameters.put(id, value);
                return true;
            case RADIUSZ:
                parameters.put(id, value);
                return true;
            case FILTER:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ClearCLBuffer execute(ClearCLBuffer input) {
        // get parameters
        int rx = getParameterInt(RADIUSXY);
        int ry = rx;
        int rz = getParameterInt(RADIUSZ);
        String filterS = getParameter(FILTER).toLowerCase();

        if (rz > 0)
            IJ.log("3D GPU Filtering with filter " + filterS + " and radii " + rx + "-" + ry + "-" + rz);
        else
            IJ.log("2D GPU Filtering with filter " + filterS + " and radii " + rx + "-" + ry);

        // init CLIJ and create images
        CLIJ2 clij = CLIJ2.getInstance();
        // get result
        ClearCLBuffer outputCLBuffer;
        if (rz > 0)
            outputCLBuffer = filter3D(clij, input, filterS, rx, ry, rz);
        else
            outputCLBuffer = filter2D(clij, input, filterS, rx, ry);
        // cleanup memory on GPU
        clij.release(input);

        return outputCLBuffer;
    }

    private ClearCLBuffer filter2D(CLIJ2 clij, ClearCLBuffer inputCLBuffer, String filter, int rx, int ry) {
        ClearCLBuffer outputCLBuffer = clij.create(inputCLBuffer);
        ClearCLBuffer tmpCLBuffer;
        boolean result = false;

        switch (filter) {
            case "median":
                result = clij.median2DSphere(inputCLBuffer, outputCLBuffer, rx, ry);
                break;
            case "mean":
                result = clij.mean2DSphere(inputCLBuffer, outputCLBuffer, rx, ry);
                break;
            case "erode":
            case "min":
                result = clij.minimum2DSphere(inputCLBuffer, outputCLBuffer, rx, ry);
                break;
            case "dilate":
            case "max":
                result = clij.maximum2DSphere(inputCLBuffer, outputCLBuffer, rx, ry);
                break;
            case "open":
                tmpCLBuffer = clij.create(inputCLBuffer);
                clij.minimum2DSphere(inputCLBuffer, tmpCLBuffer, rx, ry);
                result = clij.maximum2DSphere(tmpCLBuffer, outputCLBuffer, rx, ry);
                clij.release(tmpCLBuffer);
                break;
            case "close":
                tmpCLBuffer = clij.create(inputCLBuffer);
                clij.maximum2DSphere(inputCLBuffer, tmpCLBuffer, rx, ry);
                result = clij.minimum2DSphere(tmpCLBuffer, outputCLBuffer, rx, ry);
                clij.release(tmpCLBuffer);
                break;
            case "tophat":
                tmpCLBuffer = clij.create(inputCLBuffer);
                ClearCLBuffer openCLBuffer = clij.create(inputCLBuffer);
                clij.minimum2DSphere(inputCLBuffer, tmpCLBuffer, rx, ry);
                clij.maximum2DSphere(tmpCLBuffer, openCLBuffer, rx, ry);
                result = clij.subtractImages(inputCLBuffer, openCLBuffer, outputCLBuffer);
                clij.release(tmpCLBuffer);
                clij.release(openCLBuffer);
                break;
        }

        if (result)
            return outputCLBuffer;

        return null;
    }


    private ClearCLBuffer filter3D(CLIJ2 clij, ClearCLBuffer inputCLBuffer, String filter, int rx, int ry, int rz) {
        ClearCLBuffer outputCLBuffer = clij.create(inputCLBuffer);
        ClearCLBuffer tmpCLBuffer;
        boolean result = false;

        switch (filter) {
            case "median":
                result = clij.median3DSphere(inputCLBuffer, outputCLBuffer, rx, ry, rz);
                break;
            case "mean":
                result = clij.mean3DSphere(inputCLBuffer, outputCLBuffer, rx, ry, rz);
                break;
            case "erode":
            case "min":
                result = clij.minimum3DSphere(inputCLBuffer, outputCLBuffer, rx, ry, rz);
                break;
            case "dilate":
            case "max":
                result = clij.maximum3DSphere(inputCLBuffer, outputCLBuffer, rx, ry, rz);
                break;
            case "open":
                tmpCLBuffer = clij.create(inputCLBuffer);
                clij.minimum3DSphere(inputCLBuffer, tmpCLBuffer, rx, ry, rz);
                result = clij.maximum3DSphere(tmpCLBuffer, outputCLBuffer, rx, ry, rz);
                clij.release(tmpCLBuffer);
                break;
            case "close":
                tmpCLBuffer = clij.create(inputCLBuffer);
                clij.maximum3DSphere(inputCLBuffer, tmpCLBuffer, rx, ry, rz);
                result = clij.minimum3DSphere(tmpCLBuffer, outputCLBuffer, rx, ry, rz);
                clij.release(tmpCLBuffer);
                break;
            case "tophat":
                tmpCLBuffer = clij.create(inputCLBuffer);
                ClearCLBuffer openCLBuffer = clij.create(inputCLBuffer);
                clij.minimum3DSphere(inputCLBuffer, tmpCLBuffer, rx, ry, rz);
                clij.maximum3DSphere(tmpCLBuffer, openCLBuffer, rx, ry, rz);
                result = clij.subtractImages(inputCLBuffer, openCLBuffer, outputCLBuffer);
                clij.release(tmpCLBuffer);
                clij.release(openCLBuffer);
                break;
        }

        if (result)
            return outputCLBuffer;

        return null;
    }

    @Override
    public String getName() {
        return "2D/3D filters with CLIJ";
    }

    @Override
    public String[] getParameters() {
        return new String[]{RADIUSXY, RADIUSZ, FILTER};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    private int getParameterInt(String id) {
        return Integer.parseInt(getParameter(id));
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {

    }
}
