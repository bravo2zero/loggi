package de.loggi.processors;

/**
 * @author CptSpaetzle
 */
public @interface AttributeDef {
    String name();
    String defaultValue() default "";
    String description();
}
