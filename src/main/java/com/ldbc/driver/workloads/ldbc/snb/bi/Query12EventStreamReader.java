package com.ldbc.driver.workloads.ldbc.snb.bi;


import com.ldbc.driver.Operation;
import com.ldbc.driver.WorkloadException;
import com.ldbc.driver.csv.charseeker.CharSeeker;
import com.ldbc.driver.csv.charseeker.CharSeekerParams;
import com.ldbc.driver.csv.charseeker.Extractors;
import com.ldbc.driver.csv.charseeker.Mark;
import com.ldbc.driver.generator.CsvEventStreamReaderBasicCharSeeker;
import com.ldbc.driver.generator.GeneratorFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Query12EventStreamReader extends BaseEventStreamReader
{
    public Query12EventStreamReader(
            InputStream parametersInputStream,
            CharSeekerParams charSeekerParams,
            GeneratorFactory gf ) throws WorkloadException
    {
        super( parametersInputStream, charSeekerParams, gf );
    }

    @Override
    Operation operationFromParameters( Object[] parameters )
    {
        return new LdbcSnbBiQuery12(
                (long) parameters[0],
                (int) parameters[1]
        );
    }

    @Override
    CsvEventStreamReaderBasicCharSeeker.EventDecoder<Object[]> decoder()
    {
        return new CsvEventStreamReaderBasicCharSeeker.EventDecoder<Object[]>()
        {
            /*
            Date
            1236219
            */
            @Override
            public Object[] decodeEvent( CharSeeker charSeeker, Extractors extractors, int[] columnDelimiters,
                    Mark mark )
                    throws IOException
            {
                long date;
                if ( charSeeker.seek( mark, columnDelimiters ) )
                {
                    date = charSeeker.extract( mark, extractors.long_() ).longValue();
                }
                else
                {
                    // if first column of next row contains nothing it means the file is finished
                    return null;
                }

                return new Object[]{date, LdbcSnbBiQuery12.DEFAULT_LIMIT};
            }
        };
    }

    @Override
    int columnCount()
    {
        return 1;
    }
}