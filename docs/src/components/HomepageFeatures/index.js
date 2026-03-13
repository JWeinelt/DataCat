import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

const FeatureList = [
  {
    title: 'Everything in your database',
    Svg: require('@site/static/img/undraw_database-tables_yft5.svg').default,
    description: (
      <>
        Manage everything of your database(s) in just one application.
      </>
    ),
  },
  {
    title: 'Secure collaboration',
    Svg: require('@site/static/img/undraw_data-processing_ohfw.svg').default,
    description: (
      <>
        Process your data in just one platform and share it with your team.
      </>
    ),
  },
  {
    title: 'Schedule SQL jobs',
    Svg: require('@site/static/img/undraw_schedule_ry1w.svg').default,
    description: (
      <>
        Schedule SQL Scripts as jobs, no matter, what type of database engine you are using.
      </>
    ),
  },
];

function Feature({Svg, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
