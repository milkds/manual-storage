import java.util.HashSet;
import java.util.Set;

public class ManualUtil {
    public static Set<String> getAllowedSystemNames() {
        Set<String> result = new HashSet<String>();

        result.add("rubis_de");
        result.add("rubis_de_en");
        result.add("google_shop_deep_de");
        result.add("google_deep_amazon_de");
        result.add("google_part_deep_de");
        result.add("google_all_de");

        return result;
    }
}
