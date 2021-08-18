package logtemperature;

import com.integpg.sensor.SensorPort;
import com.integpg.system.JANOS;
import java.io.IOException;
import static java.lang.Thread.sleep;

public class LogTemperature {

    /**
     * For the main function, if at least one temperature probe is connected to
     * the JNIOR, will loop every minute to log the current temperature being
     * recorded by the temperature probe
     * @param args
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        //Declare StringBuilder for string we will log
        StringBuilder tempLog = new StringBuilder();
        while (true) {
            // Call isTempPresent function, supplying it with integers 1 - 10
            // This checks from 1 - 10 if a device type with that index exists
            for (int i = 1; i <= 10; i++) {
                if (isTempPresent(i)) {
                   
                    try {
                        String temp = Double.toString(SensorPort.getTemp(i - 1));
                        //if statment case to handle inaccurate read
                        if (!temp.equals("85.000000")) {
                            if (0 != tempLog.length()) {tempLog.append(", ");}
                            tempLog.append(i);
                            tempLog.append(" : ");
                            tempLog.append(temp);}}
                    catch (Exception e) {
                       tempLog.append("");
                    }                
                }
            }
            //Log temperature to a log file called tempLog.log
            JANOS.logfile("/temp/tempLog.log", tempLog.toString());
            tempLog.setLength(0);

            //Get the temperature probes value every minute.
            sleep(60000);
        }

    }

    /**
     * a convenience call to see if there is a temperature probe connected at
     * the given index
     *
     * @param index
     * @return
     */
    private static boolean isTempPresent(int index) {
        return isTypePresent("28", index);
    }

    /**
     * *
     * This method returns whether or not there is a device connected for the
     * given deviceType at the given index
     *
     * @param deviceType
     * @param index
     * @return
     */
    private static boolean isTypePresent(String deviceType, int index) {
        boolean present = false;

        // make sure there are devices that are present on the sensor port.  
        // we know there there are devices connected if the $present key is 
        // non blank
        String presentDevicesRegistryKey = "externals/$present";
        String presentDevicesString
                = JANOS.getRegistryString(presentDevicesRegistryKey, "");
        if (!"".equals(presentDevicesString)) {

            // check to see if there is a stored address for the temp probe at 
            // the given index
            String registryKey = "externals/deviceorder/type" + deviceType + "_" + index;
            String addressString = JANOS.getRegistryString(registryKey, "");
            if (!"".equals(addressString)) {

                // check to see if the returned address is contained in the 
                // present devices string
                present = -1 != presentDevicesString.indexOf(addressString);

            }

        }

        return present;
    }

    
}
