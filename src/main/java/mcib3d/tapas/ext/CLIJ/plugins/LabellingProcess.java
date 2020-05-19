package mcib3d.tapas.ext.CLIJ.plugins;

import mcib3d.tapas.ext.CLIJ.core.CLIJ_Utils;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.ext.CLIJ.core.TapasProcessingCLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;

public class LabellingProcess implements TapasProcessingCLIJ {

    public LabellingProcess() {
        if (!CLIJ_Utils.checkInstall()) return;
    }

    @Override
    public boolean setParameter(String id, String value) {
        return false;
    }

    @Override
    public ClearCLBuffer execute(ClearCLBuffer input) {
        // init CLIJ and create images
        CLIJ2 clij = CLIJ2.getInstance();
        // get result
        ClearCLBuffer outputCLBuffer = clij.create(input);

        if(clij.connectedComponentsLabelingDiamond(input,outputCLBuffer)){
            clij.release(input);
            return outputCLBuffer;
        }
        clij.release(input);

        return null;
    }

    @Override
    public String getName() {
        return "Labelling diamond from CLIJ";
    }

    @Override
    public String[] getParameters() {
        return new String[0];
    }

    @Override
    public String getParameter(String id) {
        return null;
    }

    @Override
    public void setCurrentImage(ImageInfo currentImage) {

    }
}
