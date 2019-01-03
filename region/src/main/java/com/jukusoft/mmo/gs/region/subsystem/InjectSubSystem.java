package com.jukusoft.mmo.gs.region.subsystem;

import java.lang.annotation.*;

/**
 * Identifies injectable {@linkplain InjectSubSystem subsystem-fields} and classes
 * with those fields.
 *
 * @see SubSystemManager#addSubSystem(Class, SubSystem)
 */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectSubSystem {

    /**
     * @return Indicates whether an annotated field/the field of the annotated
     *         type can be null. If set to true, an
     *         {@linkplain RequiredSubSystemNotFoundException} is thrown, if the subsystem doesn't exists.
     *         Default value: true.
     */
    boolean nullable() default true;

}
