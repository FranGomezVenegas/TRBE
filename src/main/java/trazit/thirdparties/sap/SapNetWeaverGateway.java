package trazit.thirdparties.sap;

//import com.sap.conn.jco.*;



public class SapNetWeaverGateway {
/*    
    public static void main(String[] args) {

        String gatewayHost = "my.gateway.server";
        String gatewayService = "sapgw00";
        String systemNumber = "00";
        String client = "100";
        String username = "myusername";
        String password = "mypassword";

        JCoDestination destination = null;
        try {
            // Set up the connection properties
            JCoDestinationManager.destinations().registerDestinationConfiguration(
                new GatewayDestinationConfiguration(gatewayHost, gatewayService, systemNumber, client, username, password)
            );

            // Get the destination
            destination = JCoDestinationManager.getDestination("MyGateway");

            // Create a function object to call a remote function
            JCoFunction function = destination.getRepository().getFunction("MY_REMOTE_FUNCTION");

            // Set the input parameters of the function
            function.getImportParameterList().setValue("MY_INPUT_PARAMETER", "Hello, World!");

            // Call the function
            function.execute(destination);

            // Get the output parameters of the function
            String outputParameter = function.getExportParameterList().getString("MY_OUTPUT_PARAMETER");

            System.out.println(outputParameter);

        } catch (JCoException e) {
            e.printStackTrace();
        } finally {
            if (destination != null) {
                try {
                    JCoContext.end(destination);
                } catch (JCoException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class GatewayDestinationConfiguration implements DestinationConfiguration {

    private String gatewayHost;
    private String gatewayService;
    private String systemNumber;
    private String client;
    private String username;
    private String password;

    public GatewayDestinationConfiguration(String gatewayHost, String gatewayService, String systemNumber, String client, String username, String password) {
        this.gatewayHost = gatewayHost;
        this.gatewayService = gatewayService;
        this.systemNumber = systemNumber;
        this.client = client;
        this.username = username;
        this.password = password;
    }

    @Override
    public Properties getDestinationProperties(String destinationName) {
        Properties props = new Properties();
        props.setProperty(DestinationDataProvider.JCO_GATEWAY_HOST, gatewayHost);
        props.setProperty(DestinationDataProvider.JCO_GATEWAY_SERVICE, gatewayService);
        props.setProperty(DestinationDataProvider.JCO_SYSNR, systemNumber);
        props.setProperty(DestinationDataProvider.JCO_CLIENT, client);
        props.setProperty(DestinationDataProvider.JCO_USER, username);
        props.setProperty(DestinationDataProvider.JCO_PASSWD, password);
        return props;
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener arg0) {}

    @Override
    public boolean supportsEvents() {
        return false;
    }
*/
}
