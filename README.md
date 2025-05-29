Pickleball Matchmaker


Description

Pickleball Matchmaker is a Spring Boot web application designed to help pickleball enthusiasts connect with other players, report match results, and track their skill progression. The app uses an Elo-based rating system to dynamically adjust player skill levels based on match outcomes.  

Features

-Player Search: Find players by zip code, skill level range, or username.

-Match Reporting: Report match results and update player skill levels based on an Elo rating system.

-Match History: View detailed match history for individual players, including opponent ratings, match outcomes, and updated skill levels.

-Pagination: Match history is paginated for easy navigation, displaying 10 results per page.

-Player List: View all registered players and click on individual profiles to see their match history.

-Authentication: Secure login system to ensure personalized access to player data.


Technologies Used

-Backend: Java, Spring Boot

-Frontend: Thymeleaf, HTML, CSS

-Database: MongoDB

-Build Tool: Maven

-Security: Spring Security

Installation

-Clone the repository:
git clone https://github.com/benjamin-r-coco_jbhunt/pickleball-matchmaker.git
-Navigate to the project directory:
cd pickleball-matchmaker
-Build the project:
mvn clean install
Run the application:
mvn spring-boot:run

Usage

-Open the application in your browser at http://localhost:8080.
-Register or log in to access the app's features.
-Use the home page to navigate to player search, match reporting, or match history.
-View and manage player profiles and match results.


License

This project is licensed under the MIT License.
