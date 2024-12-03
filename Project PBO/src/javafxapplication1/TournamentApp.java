package javafxapplication1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TournamentApp extends Application {

    private Tournament tournament = new Tournament();

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: white;");

        Label titleLabel = new Label("=== MENU TURNAMEN ===");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #607D8B;");

        Button btnRegisterTeam = new Button("Daftarkan Tim");
        Button btnAddPlayer = new Button("Tambahkan Pemain ke Tim");
        Button btnScheduleMatch = new Button("Jadwalkan Pertandingan");
        Button btnRecordScore = new Button("Catat Skor Pertandingan");
        Button btnShowStandings = new Button("Tampilkan Klasemen");
        Button btnShowMatches = new Button("Tampilkan Semua Pertandingan");
        Button btnExit = new Button("Keluar");

        btnRegisterTeam.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white;");
        btnAddPlayer.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white;");
        btnScheduleMatch.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white;");
        btnRecordScore.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white;");
        btnShowStandings.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white;");
        btnShowMatches.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white;");
        btnExit.setStyle("-fx-background-color: #607D8B; -fx-text-fill: white;");

        root.getChildren().addAll(
                titleLabel, btnRegisterTeam, btnAddPlayer, btnScheduleMatch,
                btnRecordScore, btnShowStandings, btnShowMatches, btnExit
        );

        btnRegisterTeam.setOnAction(e -> registerTeam());
        btnAddPlayer.setOnAction(e -> addPlayer());
        btnScheduleMatch.setOnAction(e -> scheduleMatch());
        btnRecordScore.setOnAction(e -> recordMatchResult());
        btnShowStandings.setOnAction(e -> showStandings());
        btnShowMatches.setOnAction(e -> showMatches());
        btnExit.setOnAction(e -> primaryStage.close());

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Aplikasi Turnamen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void registerTeam() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Daftarkan Tim");
        dialog.setHeaderText("Masukkan nama tim:");
        dialog.setContentText("Nama Tim:");

        dialog.showAndWait().ifPresent(name -> {
            Team newTeam = new Team("T" + (tournament.getTeams().size() + 1), name, "Pelatih Default");
            tournament.registerTeam(newTeam);
            System.out.println("Tim '" + name + "' berhasil didaftarkan.");
        });
    }

    private void addPlayer() {
        TextInputDialog teamDialog = new TextInputDialog();
        teamDialog.setTitle("Tambahkan Pemain");
        teamDialog.setHeaderText("Masukkan ID Tim:");
        teamDialog.setContentText("ID Tim:");

        teamDialog.showAndWait().ifPresent(teamId -> {
            TextInputDialog playerDialog = new TextInputDialog();
            playerDialog.setTitle("Tambahkan Pemain");
            playerDialog.setHeaderText("Masukkan Nama Pemain:");
            playerDialog.setContentText("Nama Pemain:");

            playerDialog.showAndWait().ifPresent(playerName -> {
                Team team = tournament.searchTeamById(teamId);
                if (team != null) {
                    team.addPlayer(playerName);
                    System.out.println("Pemain '" + playerName + "' berhasil ditambahkan ke tim " + team.getName());
                } else {
                    System.out.println("Tim dengan ID " + teamId + " tidak ditemukan.");
                }
            });
        });
    }

    private void scheduleMatch() {
        ChoiceDialog<String> teamDialog = new ChoiceDialog<>();
        teamDialog.setTitle("Jadwalkan Pertandingan");
        teamDialog.setHeaderText("Pilih dua tim yang akan bertanding:");

        List<String> teamNames = new ArrayList<>();
        for (Team team : tournament.getTeams()) {
            teamNames.add(team.getTeamId() + ": " + team.getName());
        }

        teamDialog.getItems().setAll(teamNames);

        teamDialog.showAndWait().ifPresent(team1Name -> {
            teamDialog.setHeaderText("Pilih tim kedua untuk bertanding:");
            teamDialog.getItems().setAll(teamNames.stream().filter(name -> !name.equals(team1Name)).collect(Collectors.toList()));
            teamDialog.showAndWait().ifPresent(team2Name -> {
                DatePicker datePicker = new DatePicker();
                datePicker.setValue(LocalDate.now());
                Alert dateAlert = new Alert(Alert.AlertType.INFORMATION);
                dateAlert.setTitle("Pilih Tanggal Pertandingan");
                dateAlert.setHeaderText("Pilih tanggal untuk pertandingan:");
                dateAlert.getDialogPane().setContent(datePicker);

                dateAlert.showAndWait().ifPresent(response -> {
                    LocalDate matchDate = datePicker.getValue();
                    Team team1 = tournament.searchTeamById(team1Name.split(":")[0].trim());
                    Team team2 = tournament.searchTeamById(team2Name.split(":")[0].trim());

                    if (team1 != null && team2 != null) {
                        String matchId = "M" + (tournament.getMatches().size() + 1);
                        Match match = new Match(matchId, team1, team2, matchDate);
                        tournament.addMatch(match);
                        System.out.println("Pertandingan antara " + team1.getName() + " dan " + team2.getName() + " dijadwalkan pada " + matchDate);
                    } else {
                        System.out.println("Tim tidak ditemukan.");
                    }
                });
            });
        });
    }

    private void recordMatchResult() {
        TextInputDialog matchDialog = new TextInputDialog();
        matchDialog.setTitle("Catat Skor Pertandingan");
        matchDialog.setHeaderText("Masukkan ID Pertandingan:");
        matchDialog.setContentText("ID Pertandingan:");

        matchDialog.showAndWait().ifPresent(matchId -> {
            Match match = tournament.getMatchById(matchId.trim());
            if (match != null && !match.isCompleted()) {
                TextInputDialog scoreDialog1 = new TextInputDialog();
                scoreDialog1.setTitle("Catat Skor");
                scoreDialog1.setHeaderText("Masukkan skor untuk tim " + match.getTeam1().getName() + ":");
                scoreDialog1.setContentText("Skor Tim 1:");

                scoreDialog1.showAndWait().ifPresent(score1 -> {
                    TextInputDialog scoreDialog2 = new TextInputDialog();
                    scoreDialog2.setTitle("Catat Skor");
                    scoreDialog2.setHeaderText("Masukkan skor untuk tim " + match.getTeam2().getName() + ":");
                    scoreDialog2.setContentText("Skor Tim 2:");

                    scoreDialog2.showAndWait().ifPresent(score2 -> {
                        try {
                            int scoreTeam1 = Integer.parseInt(score1.trim());
                            int scoreTeam2 = Integer.parseInt(score2.trim());

                            // Update skor dan status pertandingan
                            match.setScores(scoreTeam1, scoreTeam2);

                            // Berikan poin berdasarkan hasil pertandingan
                            if (scoreTeam1 > scoreTeam2) {
                                match.getTeam1().updatePoints(3);
                            } else if (scoreTeam1 < scoreTeam2) {
                                match.getTeam2().updatePoints(3);
                            } else {
                                match.getTeam1().updatePoints(1);
                                match.getTeam2().updatePoints(1);
                            }

                            System.out.println("Skor berhasil dicatat untuk pertandingan " + matchId);
                        } catch (NumberFormatException e) {
                            System.out.println("Skor harus berupa angka.");
                        }
                    });
                });
            } else {
                System.out.println("Pertandingan tidak ditemukan atau sudah selesai.");
            }
        });
    }

    private void showStandings() {
        StringBuilder standings = new StringBuilder("=== Klasemen ===\n\n");
        tournament.getStandings().forEach(team ->
                standings.append(team.getName()).append(" - ").append(team.getPoints()).append(" poin\n")
        );

        Alert standingsAlert = new Alert(Alert.AlertType.INFORMATION);
        standingsAlert.setTitle("Klasemen Turnamen");
        standingsAlert.setHeaderText("Daftar Klasemen");
        standingsAlert.setContentText(standings.toString());
        standingsAlert.showAndWait();
    }

    private void showMatches() {
        StringBuilder completedMatches = new StringBuilder("=== Pertandingan Selesai ===\n\n");
        StringBuilder upcomingMatches = new StringBuilder("=== Pertandingan Belum Selesai ===\n\n");

        tournament.getMatches().forEach(match -> {
            if (match.isCompleted()) {
                completedMatches.append(match.toString()).append("\n");
            } else {
                upcomingMatches.append(match.toString()).append("\n");
            }
        });

        Alert matchesAlert = new Alert(Alert.AlertType.INFORMATION);
        matchesAlert.setTitle("Semua Pertandingan");
        matchesAlert.setHeaderText("Daftar Semua Pertandingan");
        matchesAlert.setContentText(completedMatches.toString() + "\n" + upcomingMatches.toString());
        matchesAlert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Tournament {
    private List<Team> teams = new ArrayList<>();
    private List<Match> matches = new ArrayList<>();

    public List<Team> getTeams() {
        return teams;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void registerTeam(Team team) {
        teams.add(team);
    }

    public Team searchTeamById(String teamId) {
        return teams.stream().filter(team -> team.getTeamId().equals(teamId)).findFirst().orElse(null);
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public Match getMatchById(String matchId) {
        return matches.stream().filter(match -> match.getMatchId().equals(matchId)).findFirst().orElse(null);
    }

    public List<Team> getStandings() {
        
        return teams.stream()
                .sorted((team1, team2) -> Integer.compare(team2.getPoints(), team1.getPoints()))
                .collect(Collectors.toList());
    }
}

class Team {
    private String teamId;
    private String name;
    private String coach;
    private List<String> players = new ArrayList<>();
    private int points = 0;

    public Team(String teamId, String name, String coach) {
        this.teamId = teamId;
        this.name = name;
        this.coach = coach;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void addPlayer(String playerName) {
        players.add(playerName);
    }

    public void updatePoints(int points) {
        this.points += points;
    }

    public List<String> getPlayers() {
        return players;
    }
}

class Match {
    private String matchId;
    private Team team1;
    private Team team2;
    private LocalDate matchDate;
    private int scoreTeam1;
    private int scoreTeam2;
    private boolean completed = false;

    public Match(String matchId, Team team1, Team team2, LocalDate matchDate) {
        this.matchId = matchId;
        this.team1 = team1;
        this.team2 = team2;
        this.matchDate = matchDate;
    }

    public String getMatchId() {
        return matchId;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setScores(int scoreTeam1, int scoreTeam2) {
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.completed = true;
    }

    @Override
    public String toString() {
        return matchId + ": " + team1.getName() + " vs " + team2.getName() + " pada " + matchDate + " | Skor: " + scoreTeam1 + " - " + scoreTeam2;
    }
}
