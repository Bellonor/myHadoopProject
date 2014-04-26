package myInputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.CombineFileRecordReader;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;


import org.apache.hadoop.util.LineReader;

public class MyInputFormat extends CombineFileInputFormat<Text, Text> {

	/**
	 *   make sure file will not be splitted
	 */
	@Override
	protected boolean isSplitable(JobContext context, Path file) {
		return false;
	}
	
	
	/**
	 *   specify record reader
	 */
	@Override
	public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException {
		CombineFileRecordReader<Text, Text> recordReader = 	new CombineFileRecordReader<Text, Text>(
				(CombineFileSplit)split, context, MyRecordReader.class);
		return recordReader;
	}

}



