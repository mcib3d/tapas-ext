package mcib3d.tapas.ext.CLIJ.plugins;

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

    public boolean setParameter(String id, String value) {
        switch (id) {
            case MEASUREMENT: // test value
                parameters.put(id, value);
                return true;
        }
        return false;
    }

    public ClearCLBuffer execute(ClearCLBuffer label_map) {
        // init CLIJ and create images
        CLIJ2 clij = CLIJ2.getInstance();

        ResultsTable table = new ResultsTable();
        // todo: if we manage to insert the original image as first parameter in the following line, we can measure
        //       intensities of objects
        clij.statisticsOfBackgroundAndLabelledPixels(label_map, label_map, table);

        String column_name = getParameter(MEASUREMENT);
        ClearCLBuffer column = clij.create(table.size(), 1, 1);
        clij.pushResultsTableColumn(column, table, column_name);

        ClearCLBuffer parametric_image = clij.create(label_map);
        clij.generateParametricImage(column, label_map, parametric_image);

        column.close();
        label_map.close();

        return parametric_image;
    }

    public String getName() {
        return "Generate parametric images with CLIJ";
    }

    public String[] getParameters() {
        return new String[]{MEASUREMENT};
    }

    public String getParameter(String id) {
        return parameters.get(id);
    }

    public void setCurrentImage(ImageInfo currentImage) {
        info = currentImage;
    }
}
