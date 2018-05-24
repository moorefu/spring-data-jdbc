/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jdbc.core.mapping;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;

/**
 * A specialized {@link ConvertingPropertyAccessor} that can handle conversions between {@link AggregateReference}s and
 * their id type. Appart from wrapping/unwrapping the {@link AggregateReference} the id value also gets converted if necessary.
 *
 * @author Jens Schauder
 * @since 1.0
 */
public class AggregateReferenceHandlingPropertyAccessor extends ConvertingPropertyAccessor {

	private final ConversionService conversionService;

	public AggregateReferenceHandlingPropertyAccessor(PersistentPropertyAccessor accessor,
			ConversionService conversionService) {

		super(accessor, conversionService);

		this.conversionService = conversionService;
	}

	@Override
	public void setProperty(PersistentProperty<?> property, Object value) {

		if (AggregateReference.class.isAssignableFrom(property.getType())) {

			Class<? extends Object> idType = property.getTypeInformation().getSuperTypeInformation(AggregateReference.class)
					.getTypeArguments().get(1).getType();

			super.setProperty(property, AggregateReference.to(conversionService.convert(value, idType)));
		} else {
			super.setProperty(property, value);
		}
	}

	@Override
	public <T> T getProperty(PersistentProperty<?> property, Class<T> targetType) {

		if (AggregateReference.class.isAssignableFrom(property.getType())) {
			return (T) conversionService.convert(((AggregateReference) super.getProperty(property)).getId(), targetType);
		}

		return super.getProperty(property, targetType);
	}
}
