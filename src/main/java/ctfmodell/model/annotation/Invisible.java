package ctfmodell.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation, um eine Police Officer Methode im Dropdown Menu nicht mehr sichtbar zu machen
 *
 * @author Nick Garbusa
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Invisible {
}
