package mcib3d.tapas.ext.CLIJ.core;

import mcib3d.tapas.core.TapasProcessorAbstract;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;

public class TapasProcessorCLIJ extends TapasProcessorAbstract<ClearCLBuffer>
{
    public TapasProcessorCLIJ() {
        super();
        setNameProcessor("CLIJ Processor");
        CLIJ_Utils.checkInstall();
    }
}
