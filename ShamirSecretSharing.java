import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        try {
            // Step 1: Parse JSON input
            JSONParser parser = new JSONParser();
            JSONObject input = (JSONObject) parser.parse(new FileReader("input.json"));
            
            JSONObject keys = (JSONObject) input.get("keys");
            int n = Integer.parseInt(keys.get("n").toString());
            int k = Integer.parseInt(keys.get("k").toString());

            // Lists to store X and Y values
            List<Integer> xValues = new ArrayList<>();
            List<BigInteger> yValues = new ArrayList<>();

            // Step 2: Read points and decode Y values
            for (int i = 1; i <= n; i++) {
                String key = String.valueOf(i);
                if (input.containsKey(key)) {
                    JSONObject point = (JSONObject) input.get(key);

                    int x = Integer.parseInt(key); // X value as an integer
                    int base = Integer.parseInt(point.get("base").toString());
                    String encodedValue = point.get("value").toString();

                    // Decode Y value using BigInteger
                    BigInteger y = new BigInteger(encodedValue, base);

                    xValues.add(x);
                    yValues.add(y);
                }
            }

            // Step 3: Calculate the constant term (c) using Lagrange Interpolation
            BigInteger constantTerm = calculateConstantTerm(xValues, yValues, k);

            // Output the constant term
            System.out.println("The constant term (c) of the polynomial is: " + constantTerm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to calculate the constant term (c) using Lagrange Interpolation.
     *
     * @param xValues List of x-coordinates
     * @param yValues List of y-coordinates (decoded)
     * @param k       Minimum number of points required to solve the polynomial
     * @return The constant term (c) as a BigInteger
     */
    private static BigInteger calculateConstantTerm(List<Integer> xValues, List<BigInteger> yValues, int k) {
        BigInteger constantTerm = BigInteger.ZERO;

        // Ensure we only use the first k points for interpolation
        for (int i = 0; i < k; i++) {
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(BigInteger.valueOf(-xValues.get(j))); // Multiply by (-x_j)
                    denominator = denominator.multiply(BigInteger.valueOf(xValues.get(i) - xValues.get(j))); // (x_i - x_j)
                }
            }

            // Lagrange basis polynomial L_i(0) = numerator / denominator
            BigInteger lagrangeCoefficient = numerator.divide(denominator);

            // Add the term y_i * L_i(0) to the constant term
            constantTerm = constantTerm.add(yValues.get(i).multiply(lagrangeCoefficient));
        }

        return constantTerm;
    }
}
