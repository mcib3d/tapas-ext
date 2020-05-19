package mcib3d.tapas.ext.CLIJ.plugins;


import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.core.TapasBatchUtils;
import mcib3d.tapas.ext.CLIJ.core.TapasProcessingCLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;

import java.util.HashMap;

public class SaveProcess implements TapasProcessingCLIJ {
    public static final String DIR = "dir";
    public static final String FILE = "file";
    public static final String FORMAT = "format";
    HashMap<String, String> parameters;
    ImageInfo info;

    public SaveProcess() {
        parameters = new HashMap<>();
        setParameter(FORMAT, "tif");
        info = new ImageInfo();
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
            case FORMAT:
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ClearCLBuffer execute(ClearCLBuffer input) {
        String name = getParameter(FILE);
        String dir = getParameter(DIR);
        // check names
        String name2 = TapasBatchUtils.analyseFileName(name, info);
        String dir2 = TapasBatchUtils.analyseDirName(dir);
        // init CLIJ and create images
        CLIJ2 clij = CLIJ2.getInstance();
        ImagePlus plus = clij.pull(input);
        // save
        FileSaver saver = new FileSaver(plus);
        boolean ok = false;
        if (plus.getNSlices() > 1) {
            if (parameters.get(FORMAT).equalsIgnoreCase("zip"))
                ok = saver.saveAsZip(dir2 + name2);
            else
                ok = saver.saveAsTiffStack(dir2 + name2);
        } else {
            if (parameters.get(FORMAT).equalsIgnoreCase("zip"))
                ok = saver.saveAsZip(dir2 + name2);
            else
                ok = saver.saveAsTiff(dir2 + name2);
        }
        if (ok) {
            IJ.log(dir2 + name2 + " saved");
            ClearCLBuffer copy=clij.create(input);
            clij.copy(input,copy);
            clij.release(input);
            return copy;
        } else {
            IJ.log("Pb with saving " + dir2 + name2);
            clij.release(input);
            return null;
        }
    }

    @Override
    public String getName() {
        return "Saving file";
    }

    @Override
    public String[] getParameters() {
        return new String[]{DIR, FILE, FORMAT};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
