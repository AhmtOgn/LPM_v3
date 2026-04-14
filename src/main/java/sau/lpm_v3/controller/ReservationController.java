package sau.lpm_v3.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
@RequiredArgsConstructor
@Slf4j
public class ReservationController {
    private final ReservationService reservationService;
    private final StudentService studentService;
    private final PlaceService placeService;

    @GetMapping("/all")
    public String getAllReservations(Model model, Authentication auth) {
        boolean isAdmin = isAdmin(auth);
        log.info("RESERVATION LIST: user=[{}], role=[{}]",
                auth.getName(), isAdmin ? "ADMIN" : "USER");
        model.addAttribute("reservations", reservationService.getAllReservations(isAdmin, auth.getName()));
        return "reservations/all";
    }

    @GetMapping("/{id}")
    public String getReservation(@PathVariable Long id, Model model, Authentication auth) {
        boolean isAdmin = isAdmin(auth);

        log.debug("RESERVATION DETAIL: id=[{}], requestedBy=[{}]", id, auth.getName());
        ReservationDTO reservation = reservationService.getReservationById(id, isAdmin, auth.getName());

        model.addAttribute("reservation", reservation);
        return "reservations/_show";
    }

    @GetMapping("/add")
    public String addReservationForm(Model model, Authentication auth) {
        boolean isAdmin = isAdmin(auth);

        model.addAttribute("reservation", new ReservationDTO());
        model.addAttribute("places", placeService.getAllPlaces());
        if (isAdmin) {
            model.addAttribute("students", studentService.getAllStudents(true, auth.getName()));
        }
        return "reservations/_add";
    }

    @PostMapping("/add")
    public String addReservation(@ModelAttribute ReservationDTO dto,
                                 Authentication auth,
                                 RedirectAttributes ra) {
        try {
            reservationService.createReservation(dto, auth);
            log.info("RESERVATION CREATED: by user=[{}]", auth.getName());
            ra.addFlashAttribute("successMessage", "Reservation created successfully.");
        } catch (ResponseStatusException ex) {
            log.warn("RESERVATION CREATE FAILED: user=[{}], reason=[{}]",
                    auth.getName(), ex.getReason());
            ra.addFlashAttribute("errorMessage", ex.getReason());
        }
        return "redirect:/reservation/all";
    }

    @GetMapping("/update/{id}")
    public String updateReservationForm(@PathVariable Long id, Model model, Authentication auth) {
        boolean isAdmin = isAdmin(auth);
        log.debug("RESERVATION UPDATE FORM: id=[{}], requestedBy=[{}]", id, auth.getName());

        model.addAttribute("reservation", reservationService.getReservationById(id, isAdmin, auth.getName()));
        model.addAttribute("places", placeService.getAllPlaces());
        return "reservations/_update";
    }

    @PostMapping("/update/{id}")
    public String updateReservation(@PathVariable Long id,
                                    @ModelAttribute ReservationDTO dto,
                                    Authentication auth,
                                    RedirectAttributes ra) {
        boolean isAdmin = isAdmin(auth);
        dto.setId(id);

        try {
            reservationService.updateReservation(id, dto, isAdmin, auth.getName());
            log.info("RESERVATION UPDATED: id=[{}], by=[{}]", id, auth.getName());
            ra.addFlashAttribute("successMessage", "Reservation updated successfully.");
        } catch (ResponseStatusException ex) {
            log.warn("RESERVATION UPDATE FAILED: id=[{}], user=[{}], reason=[{}]",
                    id, auth.getName(), ex.getReason());
            ra.addFlashAttribute("errorMessage", ex.getReason());
        }
        return "redirect:/reservation/all";
    }

    @DeleteMapping("/cancel/{id}")
    @ResponseBody
    public ResponseEntity<String> cancel(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = isAdmin(auth);
        try {
            reservationService.cancelReservation(id, isAdmin, auth.getName());
            log.info("RESERVATION CANCELLED: id=[{}], by=[{}]", id, auth.getName());
            return ResponseEntity.ok("Reservation cancelled.");
        } catch (ResponseStatusException ex) {
            log.warn("RESERVATION CANCEL FAILED: id=[{}], user=[{}], status=[{}], reason=[{}]",
                    id, auth.getName(), ex.getStatusCode().value(), ex.getReason());
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(ex.getReason());
        }
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}