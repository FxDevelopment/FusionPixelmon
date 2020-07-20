package me.fusiondev.fusionpixelmon.data;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import me.fusiondev.fusionpixelmon.api.files.FileUtils;
import me.fusiondev.fusionpixelmon.api.files.IFileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class ArcPlateData {

    private static final IFileUtils UTILS = new FileUtils();

    private final File FILE;
    private JSONObject data;

    public ArcPlateData(String path, Pokemon pokemon) {
        this.FILE = new File(path, pokemon.getUUID() + ".json");
        get();
    }

    public ArcPlateData(File file, Pokemon pokemon) {
        this.FILE = new File(file, pokemon.getUUID() + ".json");
        get();
    }

    /**
     * Gets the data from the saved file and populates the {@link #data} variable.
     * If the data file doesn't exist, the data is built per default {@link #build()}.
     */
    public void get() {
        if (!FILE.exists()) data = build();
        else
            try {
                data = UTILS.read(FILE.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }

    /**
     * Builds the default structure of the data.
     * @return the data JSONObject.
     */
    private JSONObject build() {
        JSONObject data = new JSONObject();

        JSONObject plates = new JSONObject();

        for (int i = 0; i < 17; i++) {
            plates.put(i + "", false);
        }

        data.put("plates", plates);
        return data;
    }

    /**
     * Saves the data to the file.
     */
    public void save() {
        UTILS.write(FILE.getAbsolutePath(), data);
    }

    /**
     * Checks if the plate in the specified index is saved in the data.
     * @param i the index of the plate.
     * @return true if the plate is saved; otherwise false.
     */
    public boolean hasPlate(int i) {
        return data.getJSONObject("plates").getBoolean(i + "");
    }

    /**
     * Adds the plate at the specified index.
     * @param i the index of the plate.
     */
    public void add(int i) {
        data.getJSONObject("plates").put(i + "", true);
    }

    /**
     * Removes the plate at the specified index.
     * @param i the index of the plate.
     */
    public void remove(int i) {
        data.getJSONObject("plates").put(i + "", false);
    }
}