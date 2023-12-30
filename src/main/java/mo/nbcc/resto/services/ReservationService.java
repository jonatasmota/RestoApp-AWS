package mo.nbcc.resto.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mo.nbcc.resto.model.Event;
import mo.nbcc.resto.model.Reservation;
import mo.nbcc.resto.model.TimeSlot;
import mo.nbcc.resto.repository.ReservationRepository;
import mo.nbcc.resto.repository.TimeSlotRepository;

/**
 * @author thewa
 *
 */
@Service
public class ReservationService {
	
	private ReservationRepository rr;
	private TimeSlotRepository tsr;
	
	@Autowired
	public ReservationService(ReservationRepository rr) {
		super();
		this.rr = rr;
	}
	
	public List<Reservation> getReservations() {
		return rr.findAll();
	}
	
	public Reservation saveReservation(Reservation t) {
		return rr.save(t);
	}
	
	public Reservation getReservationByID(Long id) {
		return rr.getReferenceById(id);
	}
	
	public void deleteReservationByID(Long id) {
		rr.deleteById(id);
	}
	
	public Reservation getReservationByConfirmationNumber(int conNumber) {		
		return rr.findByConfirmationNumber(conNumber);
	}
	
	public List<Reservation> getReservationsByName(String firstName, String lastName) {
		List<Reservation> resByName;
		
		if(firstName != null && lastName != null) {
			resByName = rr.findByFirstNameAndLastName(firstName, lastName);
		} else if (firstName != null) {
			resByName = rr.findByFirstName(firstName);
		} else if (lastName != null) {
			resByName = rr.findByLastName(lastName);
		} else {
			resByName = new ArrayList<>();
		}
		
		return resByName;
	}
	
	public List<Reservation> searchReservations(String searchQuery) {
		List<Reservation> allReservations = rr.findAll();
		List<Reservation> filteredReservations = new ArrayList<Reservation>();
		
		//Splits the query string into a list of strings with a blank space as the separator
		String[] searchQueries = searchQuery.split("\\s+");
		
		if (searchQueries.length <= 0) {
			filteredReservations = allReservations;
		} else {
			for (Reservation res : allReservations) {
				for (String query : searchQueries) {
					query.toLowerCase();
					if (
							((isInt(query) && Integer.parseInt(searchQuery) == res.getConfirmationNumber())
							|| res.getFirstName().toLowerCase().contains(query) 
							|| res.getLastName().toLowerCase().contains(query))
							&& filteredReservations.indexOf(res) == -1
					) {
						
						filteredReservations.add(res);
					}
				}
			}
		}
		return filteredReservations;
	}
	
	public List<Reservation> getReservationsByNameAndDate(LocalDate date, String searchQuery) {
		List<Reservation> reservationsByDate = this.getReservationsByDate(date);
		List<Reservation> filteredReservations = new ArrayList<>();
		
		String[] searchQueries = searchQuery.split("\\s+");
		
		for (Reservation res : reservationsByDate) {
			for (String query : searchQueries) {
				if (res.getConfirmationNumber() == Integer.parseInt(query) || res.getFirstName().contains(query) || res.getLastName().contains(query)) {
					filteredReservations.add(res);
				}
			}
		}
		
		return filteredReservations;
	}
	
	public int generateConfirmationNumber() {
		int code = 1 + (int)(Math.random() * 1000000);
		return code;
	}
	
	public LocalDate getReservationDate(Reservation reservation) {
		TimeSlot timeslot = reservation.getTimeSlot();
		Event event = timeslot.getEvent();
		
		return event.getEventDate();
	}
	
	public List<Reservation> getReservationsByDate(LocalDate date) {
		List<Reservation> allReservations = rr.findAll();
		List<Reservation> reservationsByDate = new ArrayList<>();
		
		for (Reservation reservation : allReservations) {
			LocalDate resDate = getReservationDate(reservation);
			if (resDate.equals(date))
				reservationsByDate.add(reservation);
		}
		
		return reservationsByDate;
	}
	
	public boolean isInt(String string) {
		try {
			int num = Integer.parseInt(string);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
