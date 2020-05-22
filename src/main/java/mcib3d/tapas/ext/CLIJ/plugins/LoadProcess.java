package mcib3d.tapas.ext.CLIJ.plugins;

import ij.IJ;
import ij.ImagePlus;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchUtils;
import mcib3d.tapas.ext.CLIJ.core.TapasProcessingCLIJ;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;

import java.util.HashMap;

public class LoadProcess implements TapasProcessingCLIJ {
    private static final String DIR = "dir";
    private static final String FILE = "file";

    HashMap<String, String> parameters;
    ImageInfo info;

    public LoadProcess() {
        parameters = new HashMap<>();
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case DIR:
                parameters.put(id, value);
                return true;
            case FILE:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ClearCLBuffer execute(ClearCLBuffer input) {
        String name = parameters.get(FILE);
        String dir = parameters.get(DIR);
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String dir2 = TapasBatchUtils.analyseDirName(dir);
        IJ.log("Loading " + dir2 + name2);
        ImagePlus plus = IJ.openImage(dir2 + name2);
        if (plus == null) {
            IJ.log("Could not load image "+dir2+" "+name2);
            return null;
        }

        // init CLIJ and create images
        CLIJ2 clij = CLIJ2.getInstance();
        ClearCLBuffer output = clij.push(plus);
        clij.release(input);

        return output;
    }

    @Override
    public String getName() {
        return "Loading from file to CLIJ";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }

}
