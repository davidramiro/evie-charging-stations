package de.volkswagen.springevchargingflagsapi;

import de.volkswagen.springevchargingflagsapi.model.Flag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FlagRepository extends JpaRepository<Flag, Integer> {
}
