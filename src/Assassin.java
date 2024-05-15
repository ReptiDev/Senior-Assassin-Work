import java.sql.Array;
import java.util.ArrayList;
import java.io.Serializable;

public class Assassin implements Serializable
{
    private final String name;
    private final Team team;
    private boolean isAlive;
    private int totalKills;
    private int killsThisRound;
    private ArrayList<Assassin> killList;


    public Assassin(String name, Team team)
    {
        this.name = name;
        this.team = team;
        this.isAlive = true;
        this.team.addMember(this);
        this.killList = new ArrayList<Assassin>();
        Management.playerList.add(this);
        Management.alivePlayers.add(this);

        String message = "Player " + name + " has been created and added to team " + team.getName();
        LogHandler.addLog(message);
    }

    public String getName()
    {
        return name;
    }

    public Team getTeam()
    {
        return team;
    }

    public boolean isAlive()
    {
        return isAlive;
    }

    public int getKillCount()
    {
        return totalKills;
    }

    public int getKillsThisRound()
    {
        return killsThisRound;
    }

    public void setKillsThisRound(int num)
    {
        killsThisRound = num;
    }

    public String toString()
    {
        return this.name + "(" + this.team.getName() + ")";
    }

    public ArrayList<Assassin> getKillList()
    {
        return killList;
    }

    public void eliminate()
    {
        if (this.isAlive)
        {
            this.isAlive = false;
            Management.alivePlayers.remove(this);
            Management.deadPlayers.add(this);
        }
        else
        {
            System.out.println("Player is already dead.");
        }
    }

    public void revive()
    {
        if(!this.isAlive)
        {
            this.isAlive = true;
            Management.deadPlayers.remove(this);
            Management.alivePlayers.add(this);

            String message = "Player " + this.getName() + " has been revived.";
            System.out.println(message);
            LogHandler.addLog(message);

            if (!this.getTeam().isAlive())
            {
                Team team = this.getTeam();
                // TEMPORARY WORK AROUND FOR A METHOD TO SET TEAM.ISALIVE TO TRUE
                team.setName(team.getName());
                Management.deadTeams.remove(team);
                Management.aliveTeams.add(team);
                message = "Team " + team.getName() + " has been revived.";
                System.out.println(message);
                LogHandler.addLog(message);
            }
        }
        else
        {
            System.out.println("Player is already alive");
        }
    }

    public void kill(Assassin target)
    {
        String message;
        // Update player stats with their kill
        this.killList.add(target);
        this.totalKills++;
        this.killsThisRound++;

        message = this.getName() + " has killed " + target.getName();
        System.out.println(message);

        // Remove person killed from corresponding lists of players, add to dead list
        for (int i = 0; i < Management.alivePlayers.size(); i++)
        {
            if(Management.alivePlayers.get(i).equals(target))
            {
                target.eliminate();
                break;
            }
        }

        // Check if target's death causes their whole team to die
        // If it does, set them as dead, add/remove from respective lists, and assign the dead team's target to the team who killed them
        if (!target.team.getMembers()[0].isAlive() && !target.team.getMembers()[1].isAlive())
        {
            target.team.eliminate();

            Management.assignFromKill(this, target);
            System.out.println("This kill fully eliminated team " + target.team.getName() + ". \nThis team's new target is: " + this.team.getTarget());

        }
        else
        {
            System.out.println("A player on the other team is still alive. Inform the group their target has not changed.");
        }
    }
}
