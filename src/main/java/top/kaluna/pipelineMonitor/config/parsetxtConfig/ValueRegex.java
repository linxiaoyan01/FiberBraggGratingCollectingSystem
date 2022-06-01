package top.kaluna.pipelineMonitor.config.parsetxtConfig;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import static top.kaluna.pipelineMonitor.config.parsetxtConfig.SearchHelper.translate;


/**
 * @author Yuery
 * @date 2022/5/22/0022 - 13:32
 */
/**
 * A config for a name/value pair
 */
public class ValueRegex extends NameValue {

    /**
     * This is the group that will be used when pulling values out of the test string.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "group")
    public int group;

    /**
     * Value to default to when no value is present in the input.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "default")
    String defaultValue;

    /**
     * The compiled regex
     */
    private Pattern compiledPattern;

    /**
     * Default constructor (needed for jaxb)
     */
    public ValueRegex() {
        super();
    }

    /**
     * @param id    The name of the id
     * @param value The value of the mapping
     */
    public ValueRegex(String id, String value) {
        super(id, value);
    }

    /**
     * Get the group value - if no group is defined, then we use 1 by default
     *
     * @return the selector group ( 1 by default)
     */
    public int getGroup() {
        return Math.max(1, group);
    }

    /**
     * Compile the regex for use later on
     *
     * @param searches the searches in the value
     */
    void compile(List<NameValue> searches) {
        if (null == compiledPattern) {
            String lineRegex = translate(getValue(), searches);
            String regex = String.format(".*?%s.*", lineRegex);
            try {
                Pattern valuePattern = Pattern.compile(regex, Pattern.DOTALL);
                validatePattern(valuePattern);
                compiledPattern = valuePattern;
            } catch (PatternSyntaxException e) {
                throw new IllegalArgumentException(
                        String.format("Pattern for '%s' is not a valid regex '%s'", getValue(), regex)
                );
            }
        }
    }

    /**
     * Validate that the pattern is acceptable - i.e. that it parses as a regex and contains a single group
     *
     * @param candidate the candidate pattern
     */
    private void validatePattern(Pattern candidate) {
        int count = candidate.matcher("").groupCount();
        if (count < getGroup()) {
            throw new IllegalArgumentException(
                    String.format("Pattern for '%s' should have at least %s group, not '%s'; in: %s",
                            this, getGroup(), count, candidate.pattern())
            );
        }
    }

    /**
     * Build the matcher for the record text
     *
     * @param recordText the record text
     * @return the matcher for this pattern
     */
    Matcher buildMatcher(String recordText) {
        return compiledPattern.matcher(recordText);
    }

    /**
     * Value to default to when no value is present in the input.
     *
     * @return value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * If a default value is set, returns true, otherwise false.
     *
     * @return true if a default value is defined, otherwise false
     */
    public boolean hasDefaultValue() {
        return defaultValue != null;
    }
}
