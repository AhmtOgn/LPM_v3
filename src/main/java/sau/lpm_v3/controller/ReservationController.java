package sau.lpm_v3.controller;

import org.springframework.security.core.Authentication;
import sau.lpm_v3.dtos.ReservationDTO;
import sau.lpm_v3.service.PlaceService;
import sau.lpm_v3.service.ReservationService;
import sau.lpm_v3.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reservation")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;
    private final StudentService studentService;
    private final PlaceService placeService;

    public ReservationController(ReservationService reservationService, StudentService studentService, PlaceService placeService) {
        this.reservationService = reservationService;
        this.studentService = studentService;
        this.placeService = placeService;
    }

    @GetMapping("/all")
    public String getAllReservations(Model model, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("reservations", reservationService.getAllReservations(isAdmin, auth.getName()));
        return "reservations/all";
    }

    @GetMapping("/{id}")
    public String getReservation(@PathVariable Long id, Model model, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Güvenli çekme: Sadece kendi rezervasyonunu veya Admin herkesinkini görebilir
        ReservationDTO reservation = reservationService.getReservationById(id, isAdmin, auth.getName());

        model.addAttribute("reservation", reservation);
        return "reservations/_show";
    }

    @GetMapping("/add")
    public String addReservation(Model model, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("reservation", new ReservationDTO());
        model.addAttribute("places", placeService.getAllPlaces());
        if (isAdmin) {
            model.addAttribute("students", studentService.getAllStudents(true, auth.getName()));
        }
        return "reservations/_add";
    }

    @PostMapping("/add")
    public String addReservation(@ModelAttribute ReservationDTO dto, Authentication auth) {
        reservationService.createReservation(dto, auth);
        return "redirect:/reservation/all";
    }

    @GetMapping("/update/{id}")
    public String updateReservation(@PathVariable Long id, Model model, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("reservation", reservationService.getReservationById(id, isAdmin, auth.getName()));
        model.addAttribute("places", placeService.getAllPlaces());
        return "reservations/_update";
    }

    @PostMapping("/update/{id}")
    public String updateReservation(@PathVariable Long id,
                                    @ModelAttribute ReservationDTO dto,
                                    Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        dto.setId(id); // URL'den gelen güvenli ID
        reservationService.updateReservation(id, dto, isAdmin, auth.getName());
        return "redirect:/reservation/all";
    }

    @DeleteMapping("/cancel/{id}")
    @ResponseBody
    public ResponseEntity<Void> cancel(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        reservationService.cancelReservation(id, isAdmin, auth.getName());
        return ResponseEntity.ok().build();
    }
}