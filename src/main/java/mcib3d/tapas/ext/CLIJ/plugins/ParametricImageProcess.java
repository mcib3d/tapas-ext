package mcib3d.tapas.ext.CLIJ.plugins;

import ij.ImageJ;
import ij.measure.ResultsTable;
import mcib3d.tapas.core.ImageInfo;
import mcib3d.tapas.ext.CLIJ.core.TapasProcessingCLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.plugins.StatisticsOfLabelledPixels;

import java.util.HashMap;

public class ParametricImageProcess implements TapasProcessingCLIJ {
    final static private String MEASUREMENT = "measurement";

    ImageInfo info;
    HashMap<String, String> parameters;

    public ParametricImageProcess() {
        parameters = new HashMap<>();
        setParameter(MEASUREMENT, "PIXEL_COUNT");

        // FYI
        System.out.println("Available statistics entries in CLIJ statistics:");
        for (StatisticsOfLabelledPixels.STATISTICS_ENTRY entry : StatisticsOfLabelledPixels.STATISTICS_ENTRY.values()) {
            System.out.println(entry);
        }
    }

    @Override
    public boolean setParameter(String id, String value) {
        switch (id) {
            case MEASUREMENT: // test value
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    @Override
    public ClearCLBuffer execute(ClearCLBuffer label_map) {
        // init CLIJ and create images
        CLIJ2 clij = CLIJ2.getInstance();

        ResultsTable table = new ResultsTable();
        // todo: if we manage to insert the original image as first parameter in the following line, we can measure
        //       intensities of objects
        clij.statisticsOfBackgroundAndLabelledPixels(label_map, label_map, table);

        //table.show("Results");

        String column_name = getParameter(MEASUREMENT);
        ClearCLBuffer column = clij.create(table.size(), 1, 1);
        clij.pushResultsTableColumn(column, table, column_name);
        //clij.show(column, "column");

        ClearCLBuffer parametric_image = clij.create(label_map.getDimensions(), clij.Float);
        //clij.generateParametricImage(column, label_map, parametric_image);
        clij.replaceIntensities(label_map, column, parametric_image);

        column.close();
        label_map.close();

        return parametric_image;
    }

    public static void main(String[] args) {
        new ImageJ();

        CLIJ2 clij2 = CLIJ2.getInstance();
        ClearCLBuffer input = clij2.pushString("" +
                "0 0 0 0 0 0\n" +
                "0 1 1 2 2 0\n" +
                "0 1 1 0 0 0\n" +
                "0 0 0 0 0 0");

        ClearCLBuffer result = new ParametricImageProcess().execute(input);

        clij2.show(result, "result");
    }

    @Override
    public String getName() {
        return "Generate parametric images with CLIJ";
    }

    @Override
    public String[] getParameters() {
        return new String[]{MEASUREMENT};
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
