package org.corant.asosat.ddd.gateway;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import java.lang.annotation.Target;

import javax.inject.Qualifier;
import javax.ws.rs.NameBinding;

@Qualifier
@Documented
@NameBinding
@Target({ TYPE, METHOD })
@Retention(value = RUNTIME)
public @interface WithOriginalJsonContextResolver {

}
