package sau.lpm_v3.controller;

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
    public String getAllReservations(Model model) {
        List<ReservationDTO> reservationDtos = reservationService.getAllReservations();
        model.addAttribute("reservations", reservationDtos);
        return "reservations/all";
    }

    @GetMapping("/{id}")
    public String getReservation(@PathVariable Long id, Model model) {
        model.addAttribute("reservation", reservationService.getReservationById(id));
        return "reservations/_show";
    }

    @GetMapping(value = "/add")
    public String addReservation(Model model) {
        model.addAttribute("reservation", new ReservationDTO());
        model.addAttribute("student", studentService.getAllStudents());
        model.addAttribute("place", placeService.getAllPlaces());
        return "reservations/_add";
    }

    @PostMapping(value = "add")
    public String addReservation(@ModelAttribute ReservationDTO reservationDto,
                                 @RequestParam Long studentId,
                                 @RequestParam Long placeId) {
        reservationDto.setStudentDto(studentService.getStudentById(studentId));
        reservationDto.setPlaceDto(placeService.getPlaceById(placeId));

        // Yeni kayıtta müsaitlik durumunu otomatik false (rezerve edildi) yapıyoruz
        reservationDto.setReserved(true);

        // Logs when add a new entity
        // It is going to add USER DETAILS who performed to action
        logger.info("A new Reservation that ID is [{}] ADDED.", reservationDto.getId());

        // Converting operating made internally
        reservationService.createReservation(reservationDto);
        return "redirect:/reservation/all";
    }

    @GetMapping("/update/{id}")
    public String updateReservation(@PathVariable Long id, Model model) {
        // Already getReservationById converts DTO
        model.addAttribute("reservation", reservationService.getReservationById(id));
        model.addAttribute("student", studentService.getAllStudents());
        model.addAttribute("place", placeService.getAllPlaces());
        return "reservations/_update";
    }

    @PostMapping("/update/{id}")
    public String updateReservation(@PathVariable Long id,
                                    @ModelAttribute ReservationDTO reservationDto,
                                    @RequestParam Long studentId,
                                    @RequestParam Long placeId) {
        // Logs when update an entity
        // It is going to add USER DETAILS who performed to action
        logger.info("Reservation that ID is [{}] UPDATED", reservationDto.getId());

        reservationDto.setStudentDto(studentService.getStudentById(studentId));
        reservationDto.setPlaceDto(placeService.getPlaceById(placeId));
        reservationDto.setId(id); // ID'yi URL'den güvenli şekilde alıp atıyoruz
        reservationService.updateReservation(id, reservationDto);
        return "redirect:/reservation/all";
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteReservation(@PathVariable Long id) {
        // Logs when delete an entity
        // It is going to add USER DETAILS who performed to action
        logger.warn("Reservation that ID is [{}] DELETED", id);

        reservationService.deleteReservation(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}