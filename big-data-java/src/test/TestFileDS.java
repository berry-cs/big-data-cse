package test;


import java.util.ArrayList;
import dataxml.*;

public class TestFileDS {
	public static void main(String[] args) {
		XMLFileDS xds = new XMLFileDS("vehicles.xml");
		Car1 c1 = xds.fetch("test.Car1", "make", "model", "city08");
		System.out.println(c1);
		
		ArrayList<Car1> cs = xds.fetchList(Car1.class, "make", "model", "city08");
		System.out.println(cs.size());
		Car1 max = cs.get(0);
		for (Car1 c : cs) {
			if (c.mpgCity > max.mpgCity) max = c;
		}
		System.out.println(max);
	}
}


/*


This XML file does not appear to have any style information associated with it. The document tree is shown below.
<vehicle>
<atvType>FFV</atvType>
<barrels08>21.974</barrels08>
<barrelsA08>7.4910000000000005</barrelsA08>
<charge120>0.0</charge120>
<charge240>0.0</charge240>
<city08>13</city08>
<city08U>13.1355</city08U>
<cityA08>9</cityA08>
<cityA08U>9.6009</cityA08U>
<cityCD>0.0</cityCD>
<cityE>0.0</cityE>
<cityUF>0.0</cityUF>
<co2>-1</co2>
<co2A>-1</co2A>
<co2TailpipeAGpm>629.5</co2TailpipeAGpm>
<co2TailpipeGpm>592.4666666666667</co2TailpipeGpm>
<comb08>15</comb08>
<comb08U>14.5028</comb08U>
<combA08>10</combA08>
<combA08U>10.5921</combA08U>
<combE>0.0</combE>
<combinedCD>0.0</combinedCD>
<combinedUF>0.0</combinedUF>
<cylinders>8</cylinders>
<displ>4.6</displ>
<drive>Rear-Wheel Drive</drive>
<emissionsList>
<emissionsInfo>
<efid>CFMXT04.65H9</efid>
<id>31873</id>
<salesArea>3</salesArea>
<score>2.0</score>
<scoreAlt>-1.0</scoreAlt>
<smartwayScore>-1</smartwayScore>
<standard>B8</standard>
<stdText>Bin 8</stdText>
</emissionsInfo>
<emissionsInfo>
<efid>CFMXT04.65H9</efid>
<id>31873</id>
<salesArea>7</salesArea>
<score>2.0</score>
<scoreAlt>-1.0</scoreAlt>
<smartwayScore>-1</smartwayScore>
<standard>B8</standard>
<stdText>Bin 8</stdText>
</emissionsInfo>
</emissionsList>
<engId>146</engId>
<eng_dscr>FFV</eng_dscr>
<evMotor/>
<feScore>-1</feScore>
<fuelCost08>3250</fuelCost08>
<fuelCostA08>4850</fuelCostA08>
<fuelType>Gasoline or E85</fuelType>
<fuelType1>Regular Gasoline</fuelType1>
<fuelType2>E85</fuelType2>
<ghgScore>-1</ghgScore>
<ghgScoreA>-1</ghgScoreA>
<guzzler/>
<highway08>17</highway08>
<highway08U>16.617</highway08U>
<highwayA08>12</highwayA08>
<highwayA08U>12.1217</highwayA08U>
<highwayCD>0.0</highwayCD>
<highwayE>0.0</highwayE>
<highwayUF>0.0</highwayUF>
<hlv>0</hlv>
<hpv>0</hpv>
<id>31873</id>
<lv2>0</lv2>
<lv4>0</lv4>
<make>Ford</make>
<mfrCode>FMX</mfrCode>
<model>E150 Van FFV</model>
<mpgData>N</mpgData>
<phevBlended>false</phevBlended>
<pv2>0</pv2>
<pv4>0</pv4>
<rangeA>330</rangeA>
<rangeCityA>0.0</rangeCityA>
<rangeHwyA>0.0</rangeHwyA>
<trans_dscr/>
<trany>Automatic 4-spd</trany>
<UCity>16.2</UCity>
<UCityA>11.7</UCityA>
<UHighway>22.9</UHighway>
<UHighwayA>16.6</UHighwayA>
<VClass>Vans, Cargo Type</VClass>
<year>2012</year>
<youSaveSpend>-5500</youSaveSpend>
<sCharger/>
<tCharger/>
</vehicle>

*/