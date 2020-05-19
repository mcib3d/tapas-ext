package mcib3d.tapas.ext.CLIJ.plugins;

import ij.ImagePlus;
import mcib3d.tapas.ext.CLIJ.core.TapasProcessingCLIJ;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchProcess;
import mcib3d.tapas.core.TapasBatchUtils;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;

import java.util.HashMap;

public class OutputProcess implements TapasProcessingCLIJ {
    public static final String PROJECT = "project";
    public static final String DATASET = "dataset";
    public static final String NAME = "name";

    HashMap<String, String> parameters;
    ImageInfo info;

    public OutputProcess() {
        parameters = new HashMap<>();
        setParameter(PROJECT, "?project?");
        setParameter(DATASET, "?dataset?");
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
        }
        return false;
    }

    @Override
    public ClearCLBuffer execute(ClearCLBuffer input) {
        // update final name
        String name = getParameter(NAME);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String project = getParameter(PROJECT);
        String project2 = TapasBatchUtils.analyseFileName(project, info);
        String dataset = getParameter(DATASET);
        String dataset2 = TapasBatchUtils.analyseFileName(dataset, info);
        // init CLIJ and create images
        CLIJ2 clij = CLIJ2.getInstance();
        ImagePlus plus = clij.pull(input);
        // output image
        TapasBatchProcess.outputImage(plus, info, project2, dataset2, name2);
        // return copy
        ClearCLBuffer copy = clij.create(input);
        clij.copy(input, copy);
        clij.release(input);

        return copy;
    }


    @Override
    public String getName() {
        return "Output image";
    }

    @Override
    public String[] getParameters() {
        return new String[]{PROJECT, DATASET, NAME};
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
