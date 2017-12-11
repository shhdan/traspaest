package rtree;

public class IndexablePoint {
	
	public int id;
	
	// The coordinates of this point
	public int[] coords;
	
	public IndexablePoint(){
		id = 0;
		coords = null;
	}
	
	public IndexablePoint(int _id, int[] _coords){
		id = _id;
		coords = _coords;
	}
	
	public final void close(){}
	
	public final double computeSqrDist(int[] _coords, int _dim){
		double sqrDist = 0;
		long temp = 0;
		for(int i = 0; i < _dim; ++i){
			temp = coords[i] - _coords[i];
			sqrDist += temp * temp;
		}
		return sqrDist;
	}
	
	public final double computeDist(int[] _coords, int _dim){
		return Math.sqrt(computeSqrDist(_coords, _dim));
	}

	public final long computeDotProduct(int[] _coords, int _dim){
		long ans = 0;
		long temp = 0;
		for(int i = 0; i < _dim; ++i){
			temp = _coords[i];
			temp *= _coords[i];
			ans += temp;
		}
		return ans;
	}
	
	public final long computeDotProduct(long[] _coords, int _dim){
		long ans = 0;
		for(int i = 0; i < _dim; ++i){
			ans += _coords[i] * _coords[i];
		}
		return ans;
	}
	
	
	
}
