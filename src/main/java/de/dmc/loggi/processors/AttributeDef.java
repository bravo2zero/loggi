package de.dmc.loggi.processors;

/**
 * @author CptSpaetzle
 */
public @interface AttributeDef {
    String name();
    String defaultValue() default "";
    String description();
}
