package mo.nbcc.resto.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mo.nbcc.resto.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	public List<Reservation> findByFirstName(String firstName);
	public List<Reservation> findByLastName(String lastName);
	public List<Reservation> findByFirstNameAndLastName(String firstName, String lastName);
	public Reservation findByConfirmationNumber(int conNum);
}
