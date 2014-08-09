#include "spatial_analysis.h"
#include "temporal_analysis.h"

#include <ctime>

using namespace cv;

int main()
{
	FilterCondition fcond0(, LT, -99999, 99999, 1, 1, true, 0, 100);
	Filter 1(ds337, fcond0);

	while(true)
	{
		while(1.has_next())
		{
			Emage e = 1.next();
			create_output(e, "D://BOPT//NextGenUI//EventShop//EventShop//releases//releaseDec//eventshoplinux//src//main//webapp//results\\1_filter");
		}
		Sleep(100);
	}
	return 0;
}
