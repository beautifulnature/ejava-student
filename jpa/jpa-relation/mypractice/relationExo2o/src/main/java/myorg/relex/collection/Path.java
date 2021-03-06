package myorg.relex.collection;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This entity class provides an example of an ordered list of child entities ordered by a business property
 * in the child entity.
 */
@Entity
@Table(name="RELATIONEX_PATH")
public class Path {

    private static final Logger log = LoggerFactory.getLogger(Path.class);

    @Id
    @GeneratedValue
    private int id;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn
    @OrderBy("number ASC")
    private List<Segment> segments;

    @Column(length=16)
    private String name;

    public int getId() { return id; }

    public List<Segment> getSegments() {
        if (segments==null) { segments = new LinkedList<Segment>(); }
        return segments; 
    }

    //private class SegmentComparator implements Comparator<Segment>
    public Path addSegment(Segment segment) {
        getSegments().add(segment);

		/*
		 * Collections.sort(segments, new Comparator<Segment>() {
		 * 
		 * @Override public int compare(Segment lhs, Segment rhs) {
		 * 
		 * if (lhs == rhs || lhs==null && rhs == null) { return 0; }
		 * 
		 * if (lhs != null && rhs == null) { return 1; }
		 * 
		 * if (lhs == null && rhs != null) { return -1; }
		 * 
		 * int result = lhs.getNumber() - rhs.getNumber();
		 * 
		 * log.debug(lhs.getClass().getSimpleName() + lhs.toString() + ".compareTo" +
		 * rhs.toString() + "=" + result );
		 * 
		 * return result; }});
		 */

        Collections.sort(segments);

        return this;
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }
}