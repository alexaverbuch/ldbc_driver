package com.ldbc.driver.workloads.ldbc.snb.bi;


import com.ldbc.driver.Operation;
import com.ldbc.driver.WorkloadException;
import com.ldbc.driver.csv.charseeker.CharSeeker;
import com.ldbc.driver.csv.charseeker.CharSeekerParams;
import com.ldbc.driver.csv.charseeker.Extractors;
import com.ldbc.driver.csv.charseeker.Mark;
import com.ldbc.driver.generator.CsvEventStreamReaderBasicCharSeeker;
import com.ldbc.driver.generator.GeneratorException;
import com.ldbc.driver.generator.GeneratorFactory;

import java.io.IOException;
import java.io.InputStream;

public class BiQuery17EventStreamReader extends BaseEventStreamReader
{
    public BiQuery17EventStreamReader(
            InputStream parametersInputStream,
            CharSeekerParams charSeekerParams,
            GeneratorFactory gf ) throws WorkloadException
    {
        super( parametersInputStream, charSeekerParams, gf );
    }

    @Override
    Operation operationFromParameters( Object[] parameters )
    {
        return new LdbcSnbBiQuery17InformationPropagationAnalysis(
                (String) parameters[0],
                (int) parameters[1],
                (int) parameters[2]
        );
    }

    @Override
    CsvEventStreamReaderBasicCharSeeker.EventDecoder<Object[]> decoder()
    {
        return new CsvEventStreamReaderBasicCharSeeker.EventDecoder<Object[]>()
        {
            @Override
            public Object[] decodeEvent( CharSeeker charSeeker, Extractors extractors, int[] columnDelimiters,
                    Mark mark )
                    throws IOException
            {
                String tag;
                if ( charSeeker.seek( mark, columnDelimiters ) )
                {
                    tag = charSeeker.extract( mark, extractors.string() ).value();
                }
                else
                {
                    // if first column of next row contains nothing it means the file is finished
                    return null;
                }

                int delta;
                if ( charSeeker.seek( mark, columnDelimiters ) )
                {
                    delta = charSeeker.extract( mark, extractors.int_() ).intValue();
                }
                else
                {
                    throw new GeneratorException( "Error retrieving delta" );
                }

                int limit;
                if ( charSeeker.seek( mark, columnDelimiters ) )
                {
                    limit = charSeeker.extract( mark, extractors.int_() ).intValue();
                }
                else
                {
                    throw new GeneratorException( "Error retrieving limit" );
                }

                return new Object[]{tag, delta, limit};
            }
        };
    }

    @Override
    int columnCount()
    {
        return 3;
    }
}
