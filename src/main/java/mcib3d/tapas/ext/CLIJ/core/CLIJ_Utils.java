package mcib3d.tapas.ext.CLIJ.core;

import ij.IJ;

public class CLIJ_Utils {

    public static boolean checkInstall() {
        // check install
        ClassLoader loader = IJ.getClassLoader();
        try {
            loader.loadClass("net.haesleinhuepf.clij2.CLIJ2");
        } catch (Exception e) {
            IJ.log("CLIJ not installed, please install from update site");
            return false;
        }

        return true;
    }
}
