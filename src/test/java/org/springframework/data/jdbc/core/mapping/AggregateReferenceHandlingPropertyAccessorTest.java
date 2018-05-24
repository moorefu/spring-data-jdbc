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

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Unit tests for {@link AggregateReferenceHandlingPropertyAccessor}.
 *
 * @author Jens Schauder
 */
public class AggregateReferenceHandlingPropertyAccessorTest {

	SoftAssertions softly = new SoftAssertions();

	ConversionService conversionService = new DefaultConversionService();

	JdbcMappingContext context = new JdbcMappingContext();
	JdbcPersistentEntity<?> entity = context.getRequiredPersistentEntity(DummyEntity.class);

	AggregateReferenceHandlingPropertyAccessor accessor = new AggregateReferenceHandlingPropertyAccessor(
			entity.getPropertyAccessor(new DummyEntity()), conversionService);

	@Test // DATAJDBC-221
	public void doesSimpleConversionOnSet() {

		final JdbcPersistentProperty property = entity.getRequiredPersistentProperty("simple");

		accessor.setProperty(property, 23);

		softly.assertThat(accessor.getProperty(property)).isEqualTo(23L);
		softly.assertThat(accessor.getProperty(property, String.class)).isEqualTo("23");

		softly.assertAll();
	}

	@Test // DATAJDBC-221
	public void convertsToAggregateReference() {

		final JdbcPersistentProperty property = entity.getRequiredPersistentProperty("reference");

		accessor.setProperty(property, 23);

		final Object propertyValue = accessor.getProperty(property);
		assertThat(propertyValue).isInstanceOf(AggregateReference.class);
		assertThat(((AggregateReference<String, Long>) propertyValue).getId()).isEqualTo(23L);
	}

	@Test // DATAJDBC-221
	public void convertsFromAggregateReference() {

		final JdbcPersistentProperty property = entity.getRequiredPersistentProperty("reference");

		accessor.setProperty(property, 23);

		assertThat(accessor.getProperty(property, String.class)).isEqualTo("23");
	}

	private static class DummyEntity {

		Long simple;
		AggregateReference<String, Long> reference;
	}
}
