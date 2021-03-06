package dashboard.view;

import DAO.DAOAppointment;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import dashboard.AvailableAppointment;
import dashboard.Specialty;
import dashboard.WeekDay;
import dashboard.appointmentCard.AppointmentCard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import utils.ControllerUtils;
import utils.DateUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

public class DashboardWeekController extends DashboardController implements Initializable {

    @FXML
    private GridPane schedule;
    @FXML
    private GridPane weekDays;
    @FXML
    private JFXButton previousW;
    @FXML
    private ImageView previousArrow, timeArrow;
    @FXML
    private Text fDay, lDay,
            fMonth, lMonth,
            fYear, lYear;

    private boolean morning;
    private GregorianCalendar dayDisplayed;
    private ArrayList<WeekDay> days;
    private String[] morningHours = {
            "8:00",
            "9:00",
            "10:00",
            "11:00",
            "12:00"
    };
    private String[] afternoonHours = {
            "13:00",
            "14:00",
            "15:00",
            "16:00",
            "17:00"
    };

    @FXML
    public void changePeriod() {
        Class<?> classDashboardController = DashboardMonthController.class;
        InputStream input;

        if (this.morning) {
            input = classDashboardController.getResourceAsStream("/resources/images/up.png");
        } else {
            input = classDashboardController.getResourceAsStream("/resources/images/down.png");
        }

        Image timeArrowImg = new Image(input);
        timeArrow.setImage(timeArrowImg);

        morning = !morning;

        displayHours();
        try {
            createCalendar();
        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void monthVision(ActionEvent event) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, IOException {
        DashboardMonthController controller = new DashboardMonthController(this.userId, dayDisplayed, selectedComboSpecialty, selectedComboDoctor);
        ControllerUtils.changeScene(controller, event, "../../dashboard/view/DashboardMonth.fxml");
    }

    @FXML
    public void nextWeek() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        dayDisplayed.add(Calendar.DATE, 1);
        createCalendar();
    }

    @FXML
    public void previousWeek() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        dayDisplayed.add(Calendar.DATE, -7);
        createCalendar();
    }

    @FXML
    public void goToday() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        dayDisplayed = new GregorianCalendar();
        createCalendar();
    }

    private void displayHours() {
        String[] hours = (morning) ? morningHours : afternoonHours;

        for (int i = 0; i < 5; i++) {
            Text text = (Text) schedule.getChildren().get(i);
            text.setText(hours[i]);
        }
    }

    @Override
    public void createCalendar() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        //Removes existing cards
        schedule.getChildren().removeIf(node -> node instanceof AppointmentCard);

        //Filters
        int specialty = specialtyComboBox.getSelectionModel().getSelectedItem().getSpecialtyId();
        int doctor = doctorComboBox.getSelectionModel().getSelectedItem().getDoctorId();

        //Goes to fisrt monday of week
        dayDisplayed.add(Calendar.DATE, -(dayDisplayed.get(Calendar.DAY_OF_WEEK) - 1));

        //Goes to monday
        GregorianCalendar startDay = DateUtils.copyGregorianCalendar(dayDisplayed);
        startDay.add(Calendar.DATE, 1);

        for (int i = 0; i < 5; i++) {
            GregorianCalendar weekDate = DateUtils.copyGregorianCalendar(startDay);

            WeekDay weekDay = new WeekDay(weekDate, this.user, this);
            days.add(weekDay);

            weekDay.setAvailableAppointments((new DAOAppointment()).getAvailableAppointments(specialty, selectedCity, weekDay, doctor));

            int time = (morning) ? 1 : 8;
            for (int j = 0; j < 9; j++) {
                AvailableAppointment availableAppointment = null;
                //If date is not past, get available appointments
                if (! DateUtils.isPast(weekDate)) availableAppointment = weekDay.getAppointment(time);

                if (availableAppointment != null) {
                    AppointmentCard card = availableAppointment.getCard();

                    card.setStartHour(availableAppointment.getTime().getInitialTime());
                    card.setEndHour(availableAppointment.getTime().getFinalTime());

                    int rowIndex;

                    if (morning) rowIndex = time - 1;
                    else rowIndex = time - 8;

                    schedule.add(card, i+2, rowIndex);
                }

                time++;
            }

            startDay.add(Calendar.DATE, 1);
        }


        Text today = null;

        for (int i = 0; i < 7; i++) {
            //Displays start day on navigation
            if (i == 0) {
                fDay.setText(String.valueOf(dayDisplayed.get(Calendar.DAY_OF_MONTH)));
                fMonth.setText(MONTH_NAME[dayDisplayed.get(Calendar.MONTH)]);
                fYear.setText(String.valueOf(dayDisplayed.get(Calendar.YEAR)));
            }
            //Displays end day on navigation
            if (i == 6) {
                lDay.setText(String.valueOf(dayDisplayed.get(Calendar.DAY_OF_MONTH)));
                lMonth.setText(MONTH_NAME[dayDisplayed.get(Calendar.MONTH)]);
                lYear.setText(String.valueOf(dayDisplayed.get(Calendar.YEAR)));
            }

            VBox vbox = (VBox) weekDays.getChildren().get(i);
            Text dayOfMonth = (Text) vbox.getChildren().get(0);
            Text dayOfWeek = (Text) vbox.getChildren().get(1);

            dayOfMonth.setText(String.valueOf(dayDisplayed.get(Calendar.DAY_OF_MONTH)));


            //Highlight on today and disable weekend
            if (today == null && DateUtils.isToday(dayDisplayed)) {

                today = dayOfMonth;

                if (i == 0 || i == 6) {
                    dayOfWeek.setStyle("-fx-fill:  #5DAFED;");
                    dayOfMonth.setStyle("-fx-fill:  #5DAFED;");
                } else {
                    dayOfWeek.setStyle("-fx-fill: " + BLUE + ";");
                    dayOfMonth.setStyle("-fx-fill: " + BLUE + ";");
                }
            } else {
                if (i == 0 || i == 6) {
                    dayOfWeek.setStyle("-fx-fill:  #b5bbbf;");
                    dayOfMonth.setStyle("-fx-fill: #b5bbbf;");
                } else {
                    dayOfWeek.setStyle("-fx-fill: " + GRAY + ";");
                    dayOfMonth.setStyle("-fx-fill: " + GRAY + ";");
                }
            }

            //Avoid forwarding week
            if (i != 6) dayDisplayed.add(Calendar.DATE, 1);
        }


        //If is today, disable previousWeek()
        Class<?> classDashboardController = DashboardMonthController.class;
        InputStream input;

        if (today != null) {
            previousW.setDisable(true);
            previousArrow.setDisable(true);
            input = classDashboardController.getResourceAsStream("/resources/images/arrow_left_disabled.png");
        } else {
            previousW.setDisable(false);
            previousArrow.setDisable(false);
            input = classDashboardController.getResourceAsStream("/resources/images/arrow_left.png");
        }

        Image previousArrowImg = new Image(input);
        previousArrow.setImage(previousArrowImg);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        /*this.specialtyComboBox.getSelectionModel().select(selectedComboSpecialty);
        this.doctorComboBox.getSelectionModel().select(selectedComboDoctor);

        }
        try {
            switchSpecialty();
            switchDoctor();
        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException | FileNotFoundException e) {
            ///e.printStackTrace();
        }*/
        morning = true;
        displayHours();
        try {
            createCalendar();
        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
            //e.printStackTrace();
        }
    }

    public DashboardWeekController(int userId) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        super(userId);
        days = new ArrayList<>();
        dayDisplayed = new GregorianCalendar();
    }

    public DashboardWeekController(int userId, GregorianCalendar date) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        super(userId);
        days = new ArrayList<>();
        dayDisplayed = date;
    }

    public DashboardWeekController(int userId, GregorianCalendar date, int currentComboSpecialty, int currentComboDoctor) throws ClassNotFoundException, NullPointerException, SQLException, InstantiationException, IllegalAccessException {
        super(userId);
        days = new ArrayList<>();
        dayDisplayed = date;
        this.selectedComboSpecialty = currentComboSpecialty;
        this.selectedComboDoctor = currentComboDoctor;
    }

}
