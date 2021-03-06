import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A list of ideas stored on the server.
 */
public class RemoteList extends UnicastRemoteObject implements RemoteListInterface {

    private static final String NO_IDEA_ID = "There is no idea linked with this identifier";
    private static int currentId = 0;
    private final List<Idea> ideas;

    /**
     * Creates a new remoteList.
     */
    public RemoteList() throws RemoteException {
        super();
        this.ideas = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Lists all ideas.
     *
     * @return an answer containing all of the ideas
     */
    public Answer list() {
        List<Object> objects = new ArrayList<>();
        synchronized (ideas) {
            for (Idea i : ideas) {
                objects.add(new Idea(i));
            }
        }
        return new Answer(Answer.NORMAL_STATUS, objects);
    }

    /**
     * Adds a new idea to the list.
     *
     * @param idea - the idea to add
     * @return an empty answer
     */
    public Answer add(Idea idea) {
        Idea i = new Idea(idea);
        i.setId(currentId++);
        ideas.add(i);
        return new Answer(Answer.NORMAL_STATUS, null);
    }

    /**
     * Adds a new participation to an idea.
     *
     * @param p - the participation to add to the idea
     * @return an empty answer
     */
    public Answer participate(Participation p) {
        try {
            synchronized (ideas) {
                ideas.stream().filter(idea -> (idea.getId() == p.getId())).findFirst().orElseThrow(RuntimeException::new).addInterested(p.getEmail());
            }
        } catch (RuntimeException e) {
            return new Answer(NO_IDEA_ID, null);
        }
        return new Answer(Answer.NORMAL_STATUS, null);
    }

    /**
     * See the emails of everyone interested in one idea.
     *
     * @param id - the id of the idea
     * @return an answer containing the emails of the people interested in the idea
     */
    public Answer seeInterested(Integer id) {
        synchronized (ideas) {
            try {
                Idea i = ideas.stream().filter(idea -> (idea.getId() == id)).findFirst().orElseThrow(RuntimeException::new);
                List<Object> objects = new ArrayList<>();
                objects.addAll(i.getInterested());
                return new Answer(Answer.NORMAL_STATUS, objects);
            } catch (RuntimeException e) {
                return new Answer(NO_IDEA_ID, null);
            }
        }
    }

}
