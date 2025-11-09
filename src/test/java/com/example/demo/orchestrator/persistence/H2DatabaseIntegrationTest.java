package com.example.demo.orchestrator.persistence;

import com.example.demo.orchestrator.persistence.test.TestEntity;
import com.example.demo.orchestrator.persistence.test.TestEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify H2 database setup.
 * Tests:
 * - H2 connection
 * - BaseEntity functionality (id, timestamps)
 * - JSON conversion (Map to JSON)
 * - Repository CRUD operations
 */
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.show-sql=true"
})
class H2DatabaseIntegrationTest {

    @Autowired
    private TestEntityRepository repository;

    @Test
    void shouldSaveAndRetrieveEntity() {
        // Given
        TestEntity entity = new TestEntity("Test Name", "Test Description");
        entity.addMetadata("key1", "value1");
        entity.addMetadata("key2", "value2");

        // When
        TestEntity saved = repository.save(entity);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test Name");
        assertThat(saved.getDescription()).isEqualTo("Test Description");
        assertThat(saved.getMetadata()).hasSize(2);
        assertThat(saved.getMetadata().get("key1")).isEqualTo("value1");
        assertThat(saved.getMetadata().get("key2")).isEqualTo("value2");
    }

    @Test
    void shouldFindByName() {
        // Given
        TestEntity entity = new TestEntity("Unique Name", "Description");
        repository.save(entity);

        // When
        Optional<TestEntity> found = repository.findByName("Unique Name");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Unique Name");
    }

    @Test
    void shouldUpdateEntity() throws InterruptedException {
        // Given
        TestEntity entity = new TestEntity("Original Name", "Original Description");
        TestEntity saved = repository.save(entity);
        Long id = saved.getId();
        var createdAt = saved.getCreatedAt();
        var originalUpdatedAt = saved.getUpdatedAt();

        // Wait a bit to ensure timestamp difference
        Thread.sleep(10);

        // When
        saved.setName("Updated Name");
        saved.addMetadata("newKey", "newValue");
        TestEntity updated = repository.save(saved);

        // Then
        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getCreatedAt()).isEqualTo(createdAt); // Should not change
        assertThat(updated.getUpdatedAt()).isAfter(originalUpdatedAt); // Should be updated
        assertThat(updated.getMetadata()).containsKey("newKey");
    }

    @Test
    void shouldDeleteEntity() {
        // Given
        TestEntity entity = new TestEntity("To Delete", "Description");
        TestEntity saved = repository.save(entity);
        Long id = saved.getId();

        // When
        repository.delete(saved);

        // Then
        Optional<TestEntity> found = repository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldHandleEmptyMetadata() {
        // Given
        TestEntity entity = new TestEntity("No Metadata", "Description");

        // When
        TestEntity saved = repository.save(entity);

        // Then
        assertThat(saved.getMetadata()).isEmpty();
    }

    @Test
    void shouldCountEntities() {
        // Given
        repository.save(new TestEntity("Entity 1", "Desc 1"));
        repository.save(new TestEntity("Entity 2", "Desc 2"));
        repository.save(new TestEntity("Entity 3", "Desc 3"));

        // When
        long count = repository.count();

        // Then
        assertThat(count).isEqualTo(3);
    }
}
