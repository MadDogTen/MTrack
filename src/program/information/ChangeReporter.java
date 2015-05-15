package program.information;

public class ChangeReporter {
    public String[] changes = new String[0];

    public void addChange(String newInfo) {
        String[] newList = new String[changes.length + 1];
        newList[0] = newInfo;
        int currentPlace = 1;
        for (String aString : changes) {
            newList[currentPlace] = aString;
            currentPlace++;
        }
        changes = newList;
    }
}
