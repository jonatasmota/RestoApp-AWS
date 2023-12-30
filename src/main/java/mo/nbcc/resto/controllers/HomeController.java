package mo.nbcc.resto.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mo.nbcc.resto.model.Event;
import mo.nbcc.resto.services.EventService;
import mo.nbcc.resto.services.ReservationService;
import mo.nbcc.resto.services.TimeSlotService;


@Controller
public class HomeController {
	ApplicationContext ac;
	ReservationService rs;
	EventService es;
	TimeSlotService ts;
	
	@Autowired
	public HomeController(ApplicationContext ac, ReservationService rs, EventService es, TimeSlotService ts) {
		super();
		this.ac = ac;
		this.rs = rs;
		this.es = es;
		this.ts = ts;
	}
	
	@GetMapping("/")
	public String home(Model model) {
		List<Event> events = es.getAllEvents();
		model.addAttribute("events", events);
		
		return "home";
	}

	@RequestMapping("/dashboard")
	public String dashBoard() {
		return "dashboard";
	}
		
}
