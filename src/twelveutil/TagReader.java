package twelveutil;

import java.util.ArrayList;

public class TagReader {
	
	//Caches tags after opening them. This way we don't have to read them from the HDD every time...
	public ArrayList<Tag> tags;
	
	public TagReader() {
		tags = new ArrayList<Tag>();
	}
	
	public Tag openTag(String file) {
		//If the tag is cached we return that.
		int i = 0;
		while(i < tags.size()) {
			if(tags.get(i).file.equals(file))
				return tags.get(i);
			i++;
		}
		//If not then we read the tag from the HDD, open it, cache it, and return it.
		Tag nt = new Tag(file);
		tags.add(nt);
		return nt;
	}
	
	public void unload() {
		//TODO: this
	}
}
