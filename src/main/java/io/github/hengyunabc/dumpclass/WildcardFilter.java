package io.github.hengyunabc.dumpclass;

import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.tools.jcore.ClassFilter;

/**
 * 
 * support ? *
 *
 */
public class WildcardFilter implements ClassFilter {

    public static final String PROPERTY_KEY = "io.github.hengyunabc.dumpclass.WildcardFilter.pattern";

    String pattern;

    public WildcardFilter() {
        this(System.getProperty(PROPERTY_KEY));
    }

    public WildcardFilter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean canInclude(InstanceKlass kls) {
        if (pattern == null) {
            return false;
        }

        String klassName = kls.getName().asString().replace('/', '.');

        return MatchUtils.wildcardMatch(klassName, pattern);
    }
}