#include "spatial_analysis.h"
#include "temporal_analysis.h"

#include <ctime>

using namespace cv;

int main()
{
	EmageIngestor ds335("D://BOPT//NextGenUI//EventShop//EventShop//releases//releaseDec//eventshoplinux//src//main//webapp//temp//ds335_Pollen");
	EmageIngestor ds335("D://BOPT//NextGenUI//EventShop//EventShop//releases//releaseDec//eventshoplinux//src//main//webapp//temp//ds335_Pollen");

	vector<ProcEmageIterator*> eits0;
	eits0.push_back(&ds336);
	eits0.push_back(&ds336);

	Aggregate 3(eits0, AggAVG);

	while(true)
	{
		while(3.has_next())
		{
			Emage e = 3.next();
			create_output(e, "D://BOPT//NextGenUI//EventShop//EventShop//releases//releaseDec//eventshoplinux//src//main//webapp//results\\3_aggregation");
		}
		Sleep(100);
	}
	return 0;
}
