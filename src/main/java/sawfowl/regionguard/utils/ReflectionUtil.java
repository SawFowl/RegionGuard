package sawfowl.regionguard.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

public class ReflectionUtil {

	/**
	 * Before using it, you should make sure that the class has only 1 method with the required return type and no arguments.
	 * @param type - T - The data type you are looking for.
	 * @param cast - C - The type of object returned if the search is successful.
	 * @param fromObject - O - The object in whose class you want to find the method.
	 */
	@SuppressWarnings("unchecked")
	public static <T, C, O> C getValueFromMethodWhithTypeNoArgs(Class<T> type, Class<C> cast, O fromObject) {
		for(Method method : fromObject.getClass().getMethods()) 
			if(method.getGenericParameterTypes().length == 0 && method.getReturnType() == type) 
				try {
					return (C) (Object) method.invoke(fromObject);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> Optional<T> findField(Object source, Class<T> clazz) {
		return Stream.of(source.getClass().getFields()).filter(field -> field.getType().equals(clazz)).findFirst().map(field -> (T) org.apache.logging.log4j.core.util.ReflectionUtil.getFieldValue(field, source));
	}

}
