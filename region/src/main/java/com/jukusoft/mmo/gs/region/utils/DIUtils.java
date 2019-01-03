package com.jukusoft.mmo.gs.region.utils;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.gs.region.subsystem.InjectSubSystem;
import com.jukusoft.mmo.gs.region.subsystem.RequiredSubSystemNotFoundException;
import com.jukusoft.mmo.gs.region.subsystem.SubSystem;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
* utils class for dependency injection
 *
 * @see <a href="https://github.com/opensourcegamedev/SpaceChaos/blob/master/engine/src/main/java/dev/game/spacechaos/engine/entity/Entity.java">https://github.com/opensourcegamedev/SpaceChaos/blob/master/engine/src/main/java/dev/game/spacechaos/engine/entity/Entity.java</a>
*/
public class DIUtils {

    protected static final String LOG_TAG = "DIUtils";

    protected DIUtils () {
        //
    }

    public static void injectSubSystems(Object target, Class cls, Map<Class,SubSystem> subSystemMap) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(cls);
        Objects.requireNonNull(subSystemMap);

        InjectSubSystem classAnnotation = (InjectSubSystem) cls.getAnnotation(InjectSubSystem.class);

        //check, if class contains fields with this annotation
        if (classAnnotation != null) {
            injectValidFieldsInClass(target, cls, classAnnotation.nullable(), true, subSystemMap);
        } else {
            injectAnnotatedFieldsInClass(target, subSystemMap);
        }
    }

    /**
     * Injects all properly annotated fields in the component Object.
     *
     * @param target The object whose fields should be injected
     *
     * @see #injectField(Object, Field, boolean, Map)
     */
    private static void injectAnnotatedFieldsInClass(Object target, Map<Class,SubSystem> subSystemMap) {
        for (Field field : target.getClass().getDeclaredFields()) {
            InjectSubSystem annotation = field.getAnnotation(InjectSubSystem.class);

            if (annotation != null && SubSystem.class.isAssignableFrom(field.getType())) {
                injectField(target, field, annotation.nullable(), subSystemMap);
            }
        }
    }

    /**
     * Injects all valid fields in the given subsystem object
     *
     * @param target The object whose field should be injected
     * @param cls class of the subsystem
     * @param nullable flag, if field can be null, if subsystem wasn't found
     * @param injectInherited Whether inherited fields from super classes should also be injected
     *
     * @see #injectField(Object, Field, boolean, Map)
     */
    private static void injectValidFieldsInClass(Object target, Class cls, boolean nullable, boolean injectInherited, Map<Class,SubSystem> subSystemMap) {
        Field[] declaredFields = cls.getDeclaredFields();
        for (int i = 0, s = declaredFields.length; s > i; i++) {
            if (SubSystem.class.isAssignableFrom(declaredFields[i].getType())) {
                InjectSubSystem fieldAnnotation = declaredFields[i].getAnnotation(InjectSubSystem.class);
                injectField(target, declaredFields[i],
                        fieldAnnotation != null ? fieldAnnotation.nullable() : nullable, subSystemMap);
            }
        }

        while (injectInherited && (cls = cls.getSuperclass()) != Object.class) {
            injectValidFieldsInClass(target, cls, nullable, injectInherited, subSystemMap);
        }
    }

    /**
     * Injects the value of the field in the given subsystem
     *
     * @param target The object whose field should be injected.
     * @param field The field which should injected
     * @param nullable flag, if field can be null, if subsystem wasn't found
     * @param subSystemMap map with all registered subsystems
     */
    private static void injectField(Object target, Field field, boolean nullable, Map<Class,SubSystem> subSystemMap) {
        //check if component present
        if (subSystemMap.containsKey(field.getType())) {
            //make field accessible, so private and protected fields can also be injected
            field.setAccessible(true);

            try {
                field.set(target, subSystemMap.get(field.getType()));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                Log.e(LOG_TAG, "IllegalArgumentException | IllegalAccessException in method DIUtils.injectField(), field: " + field.getName() + ", target class: " + target.getClass().getCanonicalName(), e);
                throw new RuntimeException("Couldn't inject component '" + field.getType() + "' in '"
                        + field.getDeclaringClass().getName() + "'. Exception: " + e.getLocalizedMessage());
            }
        } else if (!nullable) {
            //subsystem isn't registered
            throw new RequiredSubSystemNotFoundException("Component '" + field.getType()
                    + "' is required by component '" + field.getDeclaringClass().getName() + "', but doesn't exist!");
        }
    }

}
