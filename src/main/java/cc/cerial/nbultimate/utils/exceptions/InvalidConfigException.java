package cc.cerial.nbultimate.utils.exceptions;

import cc.cerial.nbultimate.managers.PluginConfig;

import java.util.List;


/**
 * This exception is thrown when {@link PluginConfig#validateConfig()} detects an invalid config value.
 */
public class InvalidConfigException extends RuntimeException {
    private final List<String> invalidValues;
    public InvalidConfigException(List<String> invalidValues) {
        super(String.format("The following configuration values are invalid:\n%s", String.join("\n", invalidValues)));
        this.invalidValues = invalidValues;
    }

    public List<String> getInvalidValues() {
        return this.invalidValues;
    }
}
