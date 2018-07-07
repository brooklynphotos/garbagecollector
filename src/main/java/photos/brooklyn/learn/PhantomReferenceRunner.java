package photos.brooklyn.learn;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;

public class PhantomReferenceRunner {
    public static void main(String[] args) {
        final ReferenceQueue<Person> queue = new ReferenceQueue();
        final List<FinalizePerson> phantomList = new ArrayList<>();
        List<Person> people = new ArrayList<>();
        // populate the lists
        for(int i=0;i<10;i++){
            final Person p = new Person();
            people.add(p);
            phantomList.add(new FinalizePerson(p, queue));
        }

        // remove the people reference for garbage collecting
        people = null;
        System.gc();

        // now see if the reference queue got filled up
        phantomList.forEach(p->{
            System.out.println(p.isEnqueued());
        });
        // call the clean up on each item of the queue
        Reference<? extends Person> referenceFromQueue;
        while((referenceFromQueue=queue.poll()) != null){
            ((FinalizePerson)referenceFromQueue).cleanup();
        }
    }
}

class FinalizePerson extends PhantomReference<Person>{

    public FinalizePerson(Person referent, ReferenceQueue<? super Person> q) {
        super(referent, q);
    }

    public void cleanup(){
        System.out.println("Cleaning up");
    }
}
