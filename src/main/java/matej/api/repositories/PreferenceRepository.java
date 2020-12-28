package matej.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import matej.models.Preference;

public interface PreferenceRepository extends JpaRepository<Preference, Integer> {

}
