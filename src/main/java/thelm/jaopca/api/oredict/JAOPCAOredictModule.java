package thelm.jaopca.api.oredict;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JAOPCAOredictModule {

	String[] modDependencies() default {};

	String[] classDependencies() default {};
}
