package mcib3d.tapas.ext.CLIJ.plugins;

import mcib3d.tapas.ext.CLIJ.core.CLIJ_Utils;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.ext.CLIJ.core.TapasProcessingCLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;

import java.util.HashMap;

public class AutoThresholdProcess implements TapasProcessingCLIJ {
    final static private String THRESHOLD_METHOD = "method";

    ImageInfo info;
    HashMap<String, String> parameters;

    public AutoThresholdProcess() {
        parameters = new HashMap<>();
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case THRESHOLD_METHOD: // test value
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ClearCLBuffer execute(ClearCLBuffer input) {
        // init CLIJ and create images
        CLIJ2 clij = CLIJ2.getInstance();
        // get result
        ClearCLBuffer outputCLBuffer = clij.create(input);

        if (clij.automaticThreshold(input, outputCLBuffer, getParameter(THRESHOLD_METHOD))) {
            clij.release(input);
            return outputCLBuffer;
        }
        clij.release(input);

        return null;
    }

    @Override
    public String getName() {
        return "Auto Thresholding with CLIJ";
    }

    @Override
    public String[] getParameters() {
        return new String[]{THRESHOLD_METHOD};
    }

    @Override
    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
