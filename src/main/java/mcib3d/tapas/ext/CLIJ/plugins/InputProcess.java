package mcib3d.tapas.ext.CLIJ.plugins;

import ij.ImagePlus;
import mcib3d.tapas.core.*;
import mcib3d.tapas.ext.CLIJ.core.TapasProcessingCLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;

import java.util.HashMap;

public class InputProcess implements TapasProcessingCLIJ {
    private static final String PROJECT = "project";
    private static final String DATASET = "dataset";
    private static final String NAME = "name";
    private static final String CHANNEL = "channel";
    private static final String FRAME = "frame";

    ImageInfo info;
    HashMap<String, String> parameters;

    public InputProcess() {
        info = new ImageInfo();
        parameters = new HashMap<>();
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
        setParameter(NAME, "?name?");
        setParameter(CHANNEL, "?channel?");
        setParameter(FRAME, "?frame?");
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case PROJECT:
                parameters.put(id, value);
                return true;
            case DATASET:
                parameters.put(id, value);
                return true;
            case NAME:
                parameters.put(id, value);
                return true;
            case CHANNEL:
                parameters.put(id, value);
                return true;
            case FRAME:
                parameters.put(id, value);
                return true;
        }
        return false;
    }


    @Override
    public ClearCLBuffer execute(ClearCLBuffer input) {
        String name = getParameter(NAME);
        String project = getParameter(PROJECT);
        String dataset = getParameter(DATASET);
        String project2 = TapasBatchUtils.analyseFileName(project, info);
        String dataset2 = TapasBatchUtils.analyseFileName(dataset, info);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        ImagePlus plus = null;

        // core input
        int c = TapasBatchUtils.analyseChannelFrameName(parameters.get(CHANNEL), info);
        int t = TapasBatchUtils.analyseChannelFrameName(parameters.get(FRAME), info);

        // get Image
        plus = TapasBatchProcess.inputImage(info, project2, dataset2, name2, c, t);
        if (plus == null) return null;
        // init CLIJ and create images
        CLIJ2 clij = CLIJ2.getInstance();
        ClearCLBuffer output = clij.push(plus);
        clij.release(input);

        return output;
    }

    @Override
    public String getName() {
        return "Input image CLIJ";
    }

    @Override
    public String[] getParameters() {
        return new String[]{PROJECT, DATASET, NAME, CHANNEL, FRAME};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }

}

