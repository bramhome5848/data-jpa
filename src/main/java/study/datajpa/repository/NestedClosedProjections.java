package study.datajpa.repository;

public interface NestedClosedProjections {

    String getUserName();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}
